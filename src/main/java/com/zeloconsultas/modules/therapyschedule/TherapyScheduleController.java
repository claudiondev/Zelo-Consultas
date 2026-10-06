package com.zeloconsultas.modules.therapyschedule;

import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.modules.therapyschedule.web.EsquemaTerapeuticoForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// Esquema terapeutico avulso, direto da ficha do paciente. Ver
// InpatientTherapyScheduleController pro fluxo equivalente em Internos.
@Controller
@RequestMapping("/pacientes/{patientId}/esquema-terapeutico")
public class TherapyScheduleController {

    private final TherapyScheduleService therapyScheduleService;
    private final PatientService patientService;

    public TherapyScheduleController(TherapyScheduleService therapyScheduleService, PatientService patientService) {
        this.therapyScheduleService = therapyScheduleService;
        this.patientService = patientService;
    }

    @GetMapping
    public String formulario(@PathVariable Long patientId, Model model) {
        model.addAttribute("pacienteNome", patientService.buscarPorId(patientId).getNome());
        model.addAttribute("comando", therapyScheduleService.novoFormulario());
        model.addAttribute("acaoUrl", "/pacientes/" + patientId + "/esquema-terapeutico");
        model.addAttribute("voltarUrl", "/pacientes/" + patientId + "/historico");
        return "therapyschedule/esquema-form";
    }

    @PostMapping
    public Object gerar(@PathVariable Long patientId,
                        @Valid @ModelAttribute("comando") EsquemaTerapeuticoForm comando,
                        BindingResult bindingResult,
                        Model model) {
        boolean semMedicamento = therapyScheduleService.semNenhumMedicamento(comando);

        if (bindingResult.hasErrors() || semMedicamento) {
            // Erro de negocio simples (nao e erro de campo), mostrado direto
            // via atributo de model — evita depender do #fields do Thymeleaf
            // pra erro "global" do BindingResult.
            if (semMedicamento) {
                model.addAttribute("erroGeral", "Adicione ao menos um medicamento com o nome preenchido.");
            }
            model.addAttribute("pacienteNome", patientService.buscarPorId(patientId).getNome());
            model.addAttribute("acaoUrl", "/pacientes/" + patientId + "/esquema-terapeutico");
            model.addAttribute("voltarUrl", "/pacientes/" + patientId + "/historico");
            return "therapyschedule/esquema-form";
        }

        var paciente = patientService.buscarPorId(patientId);
        byte[] pdf = therapyScheduleService.gerarPdf(paciente.getNome(), comando);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=esquema-terapeutico-paciente-" + patientId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
