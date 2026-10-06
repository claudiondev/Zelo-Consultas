package com.zeloconsultas.shared.backup;

import com.zeloconsultas.config.ProntuarioProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Stream;

// Backup camada 2: copia pro pendrive — a unica que sobrevive a uma tragedia
// fisica com o notebook. Pensado pra NUNCA sobrescrever a unica copia boa por
// uma pela metade: escreve num .tmp e so promove pro nome final com rename
// atomico (existe copia inteira ou copia nenhuma, nunca um zip truncado).
@Service
public class PendriveBackupService {

    private static final Logger log = LoggerFactory.getLogger(PendriveBackupService.class);
    static final String PREFIXO = "zelo-consultas-";
    static final String SUFIXO = ".zip";
    private static final String ARQUIVO_ULTIMA_COPIA = ".ultima-copia-pendrive";

    private final JdbcTemplate jdbcTemplate;
    private final ProntuarioProperties properties;

    public PendriveBackupService(JdbcTemplate jdbcTemplate, ProntuarioProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    // Copia o banco pro diretorio de destino (a raiz do pendrive detectado).
    // Retorna o caminho final da copia.
    public Path copiarPara(Path destino) {
        if (!Files.isDirectory(destino)) {
            throw new IllegalArgumentException("Destino do backup nao e um diretorio acessivel: " + destino);
        }

        String nomeFinal = PREFIXO + LocalDate.now() + SUFIXO;
        Path alvoFinal = destino.resolve(nomeFinal);
        Path temporario = destino.resolve(nomeFinal + ".tmp");

        String caminhoTmp = temporario.toAbsolutePath().toString();
        if (caminhoTmp.contains("'")) {
            throw new IllegalStateException("Caminho de backup invalido (contem aspas): " + caminhoTmp);
        }

        try {
            // Zip fresco e integro escrito direto no .tmp do pendrive.
            jdbcTemplate.execute("BACKUP TO '" + caminhoTmp + "'");
            // Promocao atomica: .tmp e o nome final estao no mesmo dispositivo.
            moverAtomico(temporario, alvoFinal);
            rotacionar(destino, properties.getBackup().getMaximoCopiasPendrive());
            registrarUltimaCopia();
            log.info("Backup copiado pro pendrive: {}", alvoFinal);
            return alvoFinal;
        } catch (RuntimeException e) {
            // Limpa o .tmp pra nao deixar lixo pela metade no pendrive.
            try {
                Files.deleteIfExists(temporario);
            } catch (IOException limpeza) {
                log.warn("Nao consegui apagar o temporario {}", temporario, limpeza);
            }
            throw e;
        }
    }

    // Quantos dias desde a ultima copia pro pendrive; vazio se nunca copiou.
    public OptionalLong diasDesdeUltimaCopia() {
        Path marcador = Path.of(properties.getBackup().getDiretorio()).resolve(ARQUIVO_ULTIMA_COPIA);
        if (!Files.exists(marcador)) {
            return OptionalLong.empty();
        }
        try {
            Instant ultima = Instant.parse(Files.readString(marcador).trim());
            return OptionalLong.of(ChronoUnit.DAYS.between(ultima, Instant.now()));
        } catch (Exception e) {
            log.warn("Nao consegui ler a data da ultima copia pro pendrive", e);
            return OptionalLong.empty();
        }
    }

    public boolean deveLembrarDeCopiar() {
        OptionalLong dias = diasDesdeUltimaCopia();
        // Se nunca copiou, tambem lembra.
        return dias.isEmpty() || dias.getAsLong() >= properties.getBackup().getLembreteDias();
    }

    private void moverAtomico(Path origem, Path destino) {
        try {
            Files.move(origem, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao promover a copia no pendrive", e);
        }
    }

    void rotacionar(Path destino, int maximo) {
        try (Stream<Path> arquivos = Files.list(destino)) {
            List<Path> copias = arquivos
                    .filter(p -> {
                        String nome = p.getFileName().toString();
                        return nome.startsWith(PREFIXO) && nome.endsWith(SUFIXO);
                    })
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();

            int excedente = copias.size() - maximo;
            for (int i = 0; i < excedente; i++) {
                try {
                    Files.deleteIfExists(copias.get(i));
                } catch (IOException e) {
                    log.warn("Nao consegui apagar copia antiga no pendrive {}", copias.get(i), e);
                }
            }
        } catch (IOException e) {
            log.warn("Nao consegui rotacionar copias no pendrive {}", destino, e);
        }
    }

    private void registrarUltimaCopia() {
        Path dir = Path.of(properties.getBackup().getDiretorio());
        try {
            Files.createDirectories(dir);
            Files.writeString(dir.resolve(ARQUIVO_ULTIMA_COPIA), Instant.now().toString());
        } catch (IOException e) {
            log.warn("Nao consegui registrar a data da ultima copia pro pendrive", e);
        }
    }
}
