package com.zeloconsultas.shared.backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Testa so a rotacao (logica de arquivos), sem depender do H2 — o BACKUP TO em
// si e validado subindo o app de verdade.
class BackupServiceTest {

    private final BackupService backupService = new BackupService(null, null);

    @Test
    void mantemApenasAsCopiasMaisRecentesEApagaAsAntigas(@TempDir Path dir) throws IOException {
        // Nomes com carimbo crescente: as "mais antigas" tem carimbo menor.
        criarCopia(dir, "prontuario-2026-01-01_10-00-00.zip");
        criarCopia(dir, "prontuario-2026-02-01_10-00-00.zip");
        criarCopia(dir, "prontuario-2026-03-01_10-00-00.zip");
        criarCopia(dir, "prontuario-2026-04-01_10-00-00.zip");
        // Arquivo que nao e backup nao pode ser tocado pela rotacao.
        Files.createFile(dir.resolve("outro-arquivo.txt"));

        backupService.rotacionar(dir, 2);

        List<String> restantes = listarNomes(dir);
        assertThat(restantes).containsExactlyInAnyOrder(
                "prontuario-2026-03-01_10-00-00.zip",
                "prontuario-2026-04-01_10-00-00.zip",
                "outro-arquivo.txt");
    }

    @Test
    void naoApagaNadaQuandoAbaixoDoLimite(@TempDir Path dir) throws IOException {
        criarCopia(dir, "prontuario-2026-01-01_10-00-00.zip");
        criarCopia(dir, "prontuario-2026-02-01_10-00-00.zip");

        backupService.rotacionar(dir, 30);

        assertThat(listarNomes(dir)).hasSize(2);
    }

    private void criarCopia(Path dir, String nome) throws IOException {
        Files.createFile(dir.resolve(nome));
    }

    private List<String> listarNomes(Path dir) throws IOException {
        try (var s = Files.list(dir)) {
            return s.map(p -> p.getFileName().toString()).toList();
        }
    }
}
