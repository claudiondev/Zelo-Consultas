package com.zeloconsultas.modules.therapyschedule;

import com.zeloconsultas.modules.inpatient.Inpatient;
import com.zeloconsultas.modules.inpatient.InpatientService;
import com.zeloconsultas.modules.therapyschedule.web.EsquemaTerapeuticoForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// Esquema terapeutico avulso pro interno — mesmo fluxo do
// TherapyScheduleController pra Patient.
@Controller
@RequestMapping("/internos/{inpatientId}/esquema-terapeutico")
public class InpatientTherapyScheduleController {

    private final TherapyScheduleService therapyScheduleService;
    private final InpatientService inpatientService;

    public InpatientTherapyScheduleController(TherapyScheduleService therapyScheduleService, InpatientService inpatientService) {
        this.therapyScheduleService = therapyScheduleService;
        this.inpatientService = inpatientService;
    }

    @GetMapping
    public String formulario(@PathVariable Long inpatientId, Model model) {
        model.addAttribute("pacienteNome", inpatientService.buscarPorId(inpatientId).getNome());
        model.addAttribute("comando", therapyScheduleService.novoFormulario());
        model.addAttribute("acaoUrl", "/internos/" + inpatientId + "/esquema-terapeutico");
        model.addAttribute("voltarUrl", "/internos/" + inpatientId);
        return "therapyschedule/esquema-form";
    }

    @PostMapping
    public Object gerar(@PathVariable Long inpatientId,
                        @Valid @ModelAttribute("comando") EsquemaTerapeuticoForm comando,
                        BindingResult bindingResult,
                        Model model) {
        Inpatient interno = inpatientService.buscarPorId(inpatientId);
        boolean semMedicamento = therapyScheduleService.semNenhumMedicamento(comando);

        if (bindingResult.hasErrors() || semMedicamento) {
            if (semMedicamento) {
                model.addAttribute("erroGeral", "Adicione ao menos um medicamento com o nome preenchido.");
            }
            model.addAttribute("pacienteNome", interno.getNome());
            model.addAttribute("acaoUrl", "/internos/" + inpatientId + "/esquema-terapeutico");
            model.addAttribute("voltarUrl", "/internos/" + inpatientId);
            return "therapyschedule/esquema-form";
        }

        byte[] pdf = therapyScheduleService.gerarPdf(interno.getNome(), comando);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=esquema-terapeutico-interno-" + inpatientId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
