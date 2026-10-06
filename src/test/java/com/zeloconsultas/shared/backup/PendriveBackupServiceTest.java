package com.zeloconsultas.shared.backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Testa a rotacao no pendrive (mecanica de arquivos). O caminho completo com
// BACKUP TO + rename atomico e exercitado subindo o app contra uma pasta que
// finge ser pendrive (ver validacao manual no relatorio).
class PendriveBackupServiceTest {

    private final PendriveBackupService service = new PendriveBackupService(null, null);

    @Test
    void rotacaoMantemSoAsCopiasMaisRecentesNoPendrive(@TempDir Path pendrive) throws IOException {
        criar(pendrive, "zelo-consultas-2026-01-01.zip");
        criar(pendrive, "zelo-consultas-2026-02-01.zip");
        criar(pendrive, "zelo-consultas-2026-03-01.zip");
        // Arquivo alheio no pendrive nao pode ser apagado pela rotacao.
        criar(pendrive, "foto-familia.jpg");

        service.rotacionar(pendrive, 2);

        assertThat(nomes(pendrive)).containsExactlyInAnyOrder(
                "zelo-consultas-2026-02-01.zip",
                "zelo-consultas-2026-03-01.zip",
                "foto-familia.jpg");
    }

    private void criar(Path dir, String nome) throws IOException {
        Files.createFile(dir.resolve(nome));
    }

    private List<String> nomes(Path dir) throws IOException {
        try (var s = Files.list(dir)) {
            return s.map(p -> p.getFileName().toString()).toList();
        }
    }
}
