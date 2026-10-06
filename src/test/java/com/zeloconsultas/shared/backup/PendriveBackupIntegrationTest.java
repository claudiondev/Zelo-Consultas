package com.zeloconsultas.shared.backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

// Exercita o caminho real da copia pro pendrive contra o H2 de verdade e uma
// pasta que finge ser o pendrive — valida BACKUP TO + rename atomico + marcador
// no Linux. So a deteccao de unidade removivel (Windows) fica sem cobertura.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:file:./target/test-db/pendrive-it;DB_CLOSE_ON_EXIT=FALSE",
        "prontuario.backup.diretorio=./target/test-backups-pendrive",
        "prontuario.login.senha=teste"
})
class PendriveBackupIntegrationTest {

    @Autowired
    private PendriveBackupService pendriveBackupService;

    @Test
    void copiaGeraZipIntegroSemDeixarTmpERegistraData(@TempDir Path pendrive) throws Exception {
        Path copia = pendriveBackupService.copiarPara(pendrive);

        assertThat(Files.exists(copia)).isTrue();
        assertThat(copia.getFileName().toString())
                .startsWith("zelo-consultas-")
                .endsWith(".zip");

        List<String> nomes;
        try (var s = Files.list(pendrive)) {
            nomes = s.map(p -> p.getFileName().toString()).toList();
        }
        // Rename atomico nao pode deixar nenhum .tmp pela metade.
        assertThat(nomes).noneMatch(n -> n.endsWith(".tmp"));

        // Zip integro e nao vazio.
        try (ZipFile zip = new ZipFile(copia.toFile())) {
            assertThat(zip.entries().hasMoreElements()).isTrue();
        }

        // Registrou a data da ultima copia (usada pelo lembrete na home).
        assertThat(pendriveBackupService.diasDesdeUltimaCopia()).isPresent();
    }
}
