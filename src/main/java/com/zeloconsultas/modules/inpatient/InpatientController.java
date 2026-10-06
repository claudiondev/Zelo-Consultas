package com.zeloconsultas.modules.inpatient;

import com.zeloconsultas.modules.inpatient.dto.InpatientSummary;
import com.zeloconsultas.modules.inpatient.web.InpatientNoteFormCommand;
import com.zeloconsultas.modules.inpatient.web.NovoInternoCommand;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/internos")
public class InpatientController {

    private final InpatientService inpatientService;

    public InpatientController(InpatientService inpatientService) {
        this.inpatientService = inpatientService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("internos", inpatientService.listarTodos());
        return "internos/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        NovoInternoCommand comando = new NovoInternoCommand();
        comando.setData(LocalDate.now());
        model.addAttribute("comando", comando);
        return "internos/novo";
    }

    @PostMapping("/novo")
    public String salvarNovo(@Valid @ModelAttribute("comando") NovoInternoCommand comando,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "internos/novo";
        }

        Inpatient interno = inpatientService.registrar(comando);

        redirectAttributes.addFlashAttribute("mensagem", "Interno cadastrado com sucesso.");
        return "redirect:/internos/" + interno.getId();
    }

    @GetMapping("/{id}")
    public String ficha(@PathVariable Long id, Model model) {
        Inpatient interno = inpatientService.buscarPorId(id);
        InpatientNoteFormCommand comando = new InpatientNoteFormCommand();
        comando.setData(LocalDate.now());

        model.addAttribute("interno", InpatientSummary.from(interno));
        model.addAttribute("notas", inpatientService.notasDoInterno(id));
        model.addAttribute("comando", comando);
        return "internos/ficha";
    }

    @PostMapping("/{id}/notas")
    public String salvarNota(@PathVariable Long id,
                              @Valid @ModelAttribute("comando") InpatientNoteFormCommand comando,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Inpatient interno = inpatientService.buscarPorId(id);
            model.addAttribute("interno", InpatientSummary.from(interno));
            model.addAttribute("notas", inpatientService.notasDoInterno(id));
            return "internos/ficha";
        }

        inpatientService.registrarNota(id, comando);

        redirectAttributes.addFlashAttribute("mensagem", "Anotacao registrada.");
        return "redirect:/internos/" + id;
    }
}
