package com.zeloconsultas.modules.certificate;

import com.zeloconsultas.modules.certificate.web.AtestadoContexto;
import com.zeloconsultas.modules.certificate.web.CertificateForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consultas/{consultationId}/atestado")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public String formulario(@PathVariable Long consultationId, Model model) {
        AtestadoContexto contexto = certificateService.prepararContexto(consultationId);
        model.addAttribute("pacienteNome", contexto.pacienteNome());
        model.addAttribute("comando", contexto.form());
        model.addAttribute("acaoUrl", "/consultas/" + consultationId + "/atestado");
        model.addAttribute("voltarUrl", "/pacientes/" + contexto.patientId() + "/historico");
        return "certificate/atestado-form";
    }

    @PostMapping
    public Object gerar(@PathVariable Long consultationId,
                        @Valid @ModelAttribute("comando") CertificateForm comando,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pacienteNome", certificateService.buscarNomePaciente(consultationId));
            model.addAttribute("acaoUrl", "/consultas/" + consultationId + "/atestado");
            model.addAttribute("voltarUrl", "/pacientes/" + certificateService.buscarPatientId(consultationId) + "/historico");
            return "certificate/atestado-form";
        }

        byte[] pdf = certificateService.gerarPdf(comando);

        // "inline" abre no visualizador do navegador (ela imprime dali); o
        // nome do arquivo ajuda caso ela salve em vez de imprimir.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=atestado-" + consultationId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
