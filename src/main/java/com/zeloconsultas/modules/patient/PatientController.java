package com.zeloconsultas.modules.patient;

import com.zeloconsultas.modules.consultation.ConsultationService;
import com.zeloconsultas.modules.patient.dto.PatientSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/pacientes")
public class PatientController {

    private final PatientService patientService;
    private final ConsultationService consultationService;

    public PatientController(PatientService patientService, ConsultationService consultationService) {
        this.patientService = patientService;
        this.consultationService = consultationService;
    }

    @GetMapping("/busca")
    public String busca(@RequestParam(required = false) String termo, Model model) {
        model.addAttribute("termo", termo);
        List<PatientSummary> resultados = (termo == null || termo.isBlank())
                ? patientService.listarTodos()
                : patientService.buscar(termo);
        model.addAttribute("resultados", resultados);
        return "pacientes/busca";
    }

    @GetMapping("/{id}/historico")
    public String historico(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", PatientSummary.from(patientService.buscarPorId(id)));
        model.addAttribute("consultas", consultationService.historicoDoPaciente(id));
        return "pacientes/historico";
    }
}
