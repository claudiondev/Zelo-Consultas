package com.zeloconsultas.modules.certificate;

import com.zeloconsultas.modules.certificate.web.AtestadoContexto;
import com.zeloconsultas.modules.certificate.web.CertificateForm;
import com.zeloconsultas.modules.patient.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// Atestado avulso: gerado direto do historico do paciente, sem depender de
// uma consulta com "dias de atestado" preenchido (ver CertificateController
// pra o fluxo ligado a uma consulta especifica).
@Controller
@RequestMapping("/pacientes/{patientId}/atestado")
public class PatientCertificateController {

    private final CertificateService certificateService;
    private final PatientService patientService;

    public PatientCertificateController(CertificateService certificateService, PatientService patientService) {
        this.certificateService = certificateService;
        this.patientService = patientService;
    }

    @GetMapping
    public String formulario(@PathVariable Long patientId, Model model) {
        AtestadoContexto contexto = certificateService.prepararContextoAvulso(patientId);
        model.addAttribute("pacienteNome", contexto.pacienteNome());
        model.addAttribute("comando", contexto.form());
        model.addAttribute("acaoUrl", "/pacientes/" + patientId + "/atestado");
        model.addAttribute("voltarUrl", "/pacientes/" + patientId + "/historico");
        return "certificate/atestado-form";
    }

    @PostMapping
    public Object gerar(@PathVariable Long patientId,
                        @Valid @ModelAttribute("comando") CertificateForm comando,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pacienteNome", patientService.buscarPorId(patientId).getNome());
            model.addAttribute("acaoUrl", "/pacientes/" + patientId + "/atestado");
            model.addAttribute("voltarUrl", "/pacientes/" + patientId + "/historico");
            return "certificate/atestado-form";
        }

        byte[] pdf = certificateService.gerarPdf(comando);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=atestado-paciente-" + patientId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
