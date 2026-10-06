package com.zeloconsultas.shared.backup;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.util.List;

@Controller
public class BackupController {

    private final RemovableDriveDetector removableDriveDetector;
    private final PendriveBackupService pendriveBackupService;

    public BackupController(RemovableDriveDetector removableDriveDetector,
                           PendriveBackupService pendriveBackupService) {
        this.removableDriveDetector = removableDriveDetector;
        this.pendriveBackupService = pendriveBackupService;
    }

    @GetMapping("/backup")
    public String pagina(Model model) {
        model.addAttribute("drives", removableDriveDetector.detectar());
        model.addAttribute("dias", pendriveBackupService.diasDesdeUltimaCopia().stream().boxed().findFirst().orElse(null));
        return "backup/index";
    }

    @PostMapping("/backup/pendrive")
    public String copiarPendrive(@RequestParam String destino, RedirectAttributes redirect) {
        // So aceita um destino que esteja de fato entre as unidades removiveis
        // detectadas agora — evita gravar em caminho arbitrario vindo do form.
        List<Path> drives = removableDriveDetector.detectar();
        boolean autorizado = drives.stream().anyMatch(d -> d.toString().equals(destino));
        if (!autorizado) {
            redirect.addFlashAttribute("erro", "Unidade nao reconhecida. Plugue o pendrive e tente de novo.");
            return "redirect:/backup";
        }

        try {
            Path copia = pendriveBackupService.copiarPara(Path.of(destino));
            redirect.addFlashAttribute("mensagem", "Copia de seguranca gravada no pendrive: " + copia.getFileName());
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Nao consegui copiar pro pendrive. Confira se ele esta plugado e tente de novo.");
        }
        return "redirect:/backup";
    }
}
