package com.zeloconsultas.shared.backup;

import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Detecta unidades removiveis (pendrive) dinamicamente — nunca fixa letra
// (E:/F:), como pede o plano. No Windows, cada pendrive aparece como uma raiz
// (D:, E:...) cujo "tipo do sistema" contem "removivel"/"removable". No Linux
// esse conceito nao existe do mesmo jeito, entao aqui retorna vazio — a
// deteccao real vale pro Windows da médica (precisa ser testada no
// hardware dela).
@Component
public class RemovableDriveDetector {

    private final FileSystemView fileSystemView;

    public RemovableDriveDetector() {
        this(FileSystemView.getFileSystemView());
    }

    // Construtor pra teste poder injetar um FileSystemView de mentira.
    RemovableDriveDetector(FileSystemView fileSystemView) {
        this.fileSystemView = fileSystemView;
    }

    public List<Path> detectar() {
        List<Path> removiveis = new ArrayList<>();
        for (File raiz : File.listRoots()) {
            String tipo = fileSystemView.getSystemTypeDescription(raiz);
            if (ehRemovivel(tipo)) {
                removiveis.add(raiz.toPath());
            }
        }
        return removiveis;
    }

    // A descricao muda de palavra dependendo da versao/idioma do Windows:
    // "Disco removivel"/"Removable Disk" em versoes mais antigas, "Unidade de
    // USB"/"USB Drive" em builds mais novos do Windows 11. Verifica as duas.
    static boolean ehRemovivel(String tipo) {
        if (tipo == null) {
            return false;
        }
        String minusculo = tipo.toLowerCase();
        return minusculo.contains("remov") || minusculo.contains("usb");
    }
}
