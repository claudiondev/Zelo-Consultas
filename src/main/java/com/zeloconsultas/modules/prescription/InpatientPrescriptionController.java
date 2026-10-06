package com.zeloconsultas.modules.prescription;

import com.zeloconsultas.modules.inpatient.Inpatient;
import com.zeloconsultas.modules.inpatient.InpatientService;
import com.zeloconsultas.modules.prescription.web.PrescriptionForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// Receituario avulso pro interno — mesmo fluxo do PrescriptionController pra
// Patient. Inpatient nao tem campo endereco nem CPF (cadastro deliberadamente
// enxuto, ver Inpatient.java): os dois sao obrigatorios no PrescriptionForm
// mesmo assim — como o Inpatient nunca tem, os campos sempre chegam vazios
// aqui e a medica digita na hora, sem salvar de volta no cadastro.
@Controller
@RequestMapping("/internos/{inpatientId}/receituario")
public class InpatientPrescriptionController {

    private final PrescriptionService prescriptionService;
    private final InpatientService inpatientService;

    public InpatientPrescriptionController(PrescriptionService prescriptionService, InpatientService inpatientService) {
        this.prescriptionService = prescriptionService;
        this.inpatientService = inpatientService;
    }

    @GetMapping
    public String formulario(@PathVariable Long inpatientId, Model model) {
        model.addAttribute("pacienteNome", inpatientService.buscarPorId(inpatientId).getNome());
        model.addAttribute("comando", prescriptionService.novoFormulario());
        model.addAttribute("acaoUrl", "/internos/" + inpatientId + "/receituario");
        model.addAttribute("voltarUrl", "/internos/" + inpatientId);
        return "prescription/receituario-form";
    }

    @PostMapping
    public Object gerar(@PathVariable Long inpatientId,
                        @Valid @ModelAttribute("comando") PrescriptionForm comando,
                        BindingResult bindingResult,
                        Model model) {
        Inpatient interno = inpatientService.buscarPorId(inpatientId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("pacienteNome", interno.getNome());
            model.addAttribute("acaoUrl", "/internos/" + inpatientId + "/receituario");
            model.addAttribute("voltarUrl", "/internos/" + inpatientId);
            return "prescription/receituario-form";
        }

        byte[] pdf = prescriptionService.gerarPdf(interno.getNome(), comando);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=receituario-interno-" + inpatientId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
