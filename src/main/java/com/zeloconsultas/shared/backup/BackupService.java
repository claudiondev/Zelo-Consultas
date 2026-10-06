package com.zeloconsultas.shared.backup;

import com.zeloconsultas.config.ProntuarioProperties;
import com.zeloconsultas.modules.consultation.ConsultationSavedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

// Backup local automatico (camada 1). Usa o comando nativo BACKUP TO do H2, que
// gera um zip integro mesmo com o banco aberto — por isso nunca copiamos o
// .mv.db na unha (poderia sair corrompido). Rotaciona pra nao encher o disco.
@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final DateTimeFormatter CARIMBO = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    static final String PREFIXO = "prontuario-";
    static final String SUFIXO = ".zip";

    private final JdbcTemplate jdbcTemplate;
    private final ProntuarioProperties properties;

    public BackupService(JdbcTemplate jdbcTemplate, ProntuarioProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    // Roda depois que a transacao da consulta comitou — o zip precisa refletir
    // dados ja gravados. Falha aqui nunca pode derrubar o fluxo: a consulta ja
    // esta salva, entao so registramos o erro.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoSalvarConsulta(ConsultationSavedEvent evento) {
        try {
            Path copia = backupLocal();
            log.info("Backup local gerado apos consulta {}: {}", evento.consultationId(), copia.getFileName());
        } catch (Exception e) {
            log.error("Falha ao gerar backup local automatico (consulta {}) — a consulta foi salva normalmente",
                    evento.consultationId(), e);
        }
    }

    public Path backupLocal() {
        Path diretorio = Path.of(properties.getBackup().getDiretorio());
        criarDiretorio(diretorio);

        Path destino = diretorio.resolve(PREFIXO + LocalDateTime.now().format(CARIMBO) + SUFIXO)
                .toAbsolutePath();
        // BACKUP TO nao aceita parametro bind (?), so literal; o caminho vem da
        // config + carimbo gerado por nos (nao entrada do usuario). Guarda extra
        // contra aspas simples pra nunca quebrar/injetar no comando.
        String caminho = destino.toString();
        if (caminho.contains("'")) {
            throw new IllegalStateException("Caminho de backup invalido (contem aspas): " + caminho);
        }
        jdbcTemplate.execute("BACKUP TO '" + caminho + "'");

        rotacionar(diretorio, properties.getBackup().getMaximoCopiasLocais());
        return destino;
    }

    // Mantem apenas as N copias mais recentes; apaga o excedente mais antigo.
    void rotacionar(Path diretorio, int maximo) {
        try (Stream<Path> arquivos = Files.list(diretorio)) {
            List<Path> copias = arquivos
                    .filter(BackupService::ehCopiaDeBackup)
                    // nome tem carimbo de data, entao ordem alfabetica = ordem cronologica
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();

            int excedente = copias.size() - maximo;
            for (int i = 0; i < excedente; i++) {
                Path antiga = copias.get(i);
                try {
                    Files.deleteIfExists(antiga);
                } catch (IOException e) {
                    log.warn("Nao consegui apagar backup antigo {}", antiga, e);
                }
            }
        } catch (IOException e) {
            log.warn("Nao consegui rotacionar backups em {}", diretorio, e);
        }
    }

    private static boolean ehCopiaDeBackup(Path p) {
        String nome = p.getFileName().toString();
        return nome.startsWith(PREFIXO) && nome.endsWith(SUFIXO);
    }

    private void criarDiretorio(Path diretorio) {
        try {
            Files.createDirectories(diretorio);
        } catch (IOException e) {
            throw new UncheckedIOException("Nao consegui criar o diretorio de backup " + diretorio, e);
        }
    }
}
