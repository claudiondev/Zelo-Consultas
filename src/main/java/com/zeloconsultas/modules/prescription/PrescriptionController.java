package com.zeloconsultas.modules.prescription;

import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.modules.prescription.web.PrescriptionForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// Receituario avulso, direto da ficha do paciente — mesmo formato do
// PatientCertificateController pro atestado avulso. Ver
// InpatientPrescriptionController pro fluxo equivalente em Internos.
@Controller
@RequestMapping("/pacientes/{patientId}/receituario")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PatientService patientService;

    public PrescriptionController(PrescriptionService prescriptionService, PatientService patientService) {
        this.prescriptionService = prescriptionService;
        this.patientService = patientService;
    }

    @GetMapping
    public String formulario(@PathVariable Long patientId, Model model) {
        var paciente = patientService.buscarPorId(patientId);
        PrescriptionForm comando = prescriptionService.novoFormulario();
        comando.setCpf(paciente.getCpf());
        comando.setEndereco(paciente.getEndereco());

        model.addAttribute("pacienteNome", paciente.getNome());
        model.addAttribute("comando", comando);
        model.addAttribute("acaoUrl", "/pacientes/" + patientId + "/receituario");
        model.addAttribute("voltarUrl", "/pacientes/" + patientId + "/historico");
        return "prescription/receituario-form";
    }

    @PostMapping
    public Object gerar(@PathVariable Long patientId,
                        @Valid @ModelAttribute("comando") PrescriptionForm comando,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pacienteNome", patientService.buscarPorId(patientId).getNome());
            model.addAttribute("acaoUrl", "/pacientes/" + patientId + "/receituario");
            model.addAttribute("voltarUrl", "/pacientes/" + patientId + "/historico");
            return "prescription/receituario-form";
        }

        var paciente = patientService.buscarPorId(patientId);
        byte[] pdf = prescriptionService.gerarPdf(paciente.getNome(), comando);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=receituario-paciente-" + patientId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
