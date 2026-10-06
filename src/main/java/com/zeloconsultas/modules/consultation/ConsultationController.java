package com.zeloconsultas.modules.consultation;

import com.zeloconsultas.modules.consultation.web.ConsultationFormCommand;
import com.zeloconsultas.modules.consultation.web.NovaConsultaCommand;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.modules.patient.dto.PatientSummary;
import com.zeloconsultas.shared.backup.PendriveBackupService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/consultas")
public class ConsultationController {

    // Guarda o formulario preenchido enquanto a médica confirma (ou nao)
    // se e o mesmo paciente de um candidato encontrado — evita redigitar tudo.
    private static final String SESSAO_CONSULTA_PENDENTE = "novaConsultaPendente";

    private final ConsultationService consultationService;
    private final PatientService patientService;
    private final PendriveBackupService pendriveBackupService;

    public ConsultationController(ConsultationService consultationService,
                                   PatientService patientService,
                                   PendriveBackupService pendriveBackupService) {
        this.consultationService = consultationService;
        this.patientService = patientService;
        this.pendriveBackupService = pendriveBackupService;
    }

    // Valor padrao da consulta médica — poupa ela de digitar o mesmo
    // valor em toda ficha nova. Continua editavel pra quando for diferente.
    private static final BigDecimal VALOR_CONSULTA_PADRAO = new BigDecimal("600.00");

    // Painel do mundo "Consultas" — antes vivia na home (ver HomeController),
    // mudou de endereco quando a home virou tela de escolha Consultas/Internos.
    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("lembrarBackup", pendriveBackupService.deveLembrarDeCopiar());
        model.addAttribute("totalPacientes", patientService.total());
        model.addAttribute("recentes", consultationService.atendimentosRecentes());
        return "consultas/inicio";
    }

    @GetMapping("/nova")
    public String formNovaConsulta(Model model) {
        NovaConsultaCommand comando = new NovaConsultaCommand();
        comando.getPatient().setValorConsultaPadrao(VALOR_CONSULTA_PADRAO);
        comando.getConsultation().setData(LocalDate.now());
        comando.getConsultation().setValor(VALOR_CONSULTA_PADRAO);
        model.addAttribute("comando", comando);
        return "consultas/nova";
    }

    @PostMapping("/nova")
    public String salvarNovaConsulta(@Valid @ModelAttribute("comando") NovaConsultaCommand comando,
                                      BindingResult bindingResult,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "consultas/nova";
        }

        List<PatientSummary> candidatos = patientService.buscarCandidatosDuplicidade(
                comando.getPatient().getNome(), comando.getPatient().getCpf());

        if (!candidatos.isEmpty()) {
            session.setAttribute(SESSAO_CONSULTA_PENDENTE, comando);
            model.addAttribute("candidatos", candidatos);
            return "consultas/duplicidade";
        }

        Consultation consulta = consultationService.registrarPrimeiraConsulta(
                comando.getPatient().toForm(), comando.getAnamnesis().toForm(), comando.getConsultation().toForm());

        redirectAttributes.addFlashAttribute("mensagem", "Consulta registrada com sucesso.");
        return "redirect:/pacientes/" + consulta.getPatient().getId() + "/historico";
    }

    @PostMapping("/nova/confirmar")
    public String confirmarDuplicidade(@RequestParam(required = false) Long pacienteExistenteId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        NovaConsultaCommand comando = (NovaConsultaCommand) session.getAttribute(SESSAO_CONSULTA_PENDENTE);
        if (comando == null) {
            // sessao expirou (ou acesso direto sem passar pelo formulario) — recomeca do zero
            redirectAttributes.addFlashAttribute("mensagem", "A sessao expirou, preencha o formulario novamente.");
            return "redirect:/consultas/nova";
        }

        // O id vem de um campo oculto na tela de duplicidade — nunca confiar
        // nele sem reconferir contra os candidatos reais, senao um valor
        // adulterado anexaria a consulta na ficha errada (ver revisao de
        // seguranca de 2026-07-13). Mesmo principio ja usado em
        // BackupController pro destino do pendrive.
        if (pacienteExistenteId != null) {
            List<PatientSummary> candidatos = patientService.buscarCandidatosDuplicidade(
                    comando.getPatient().getNome(), comando.getPatient().getCpf());
            boolean candidatoValido = candidatos.stream().anyMatch(c -> c.id().equals(pacienteExistenteId));
            if (!candidatoValido) {
                redirectAttributes.addFlashAttribute("mensagem",
                        "Paciente selecionado nao confere mais com a busca. Preencha o formulario novamente.");
                return "redirect:/consultas/nova";
            }
        }

        session.removeAttribute(SESSAO_CONSULTA_PENDENTE);

        Consultation consulta = (pacienteExistenteId != null)
                ? consultationService.registrarRetorno(pacienteExistenteId, comando.getConsultation().toForm())
                : consultationService.registrarPrimeiraConsulta(
                        comando.getPatient().toForm(), comando.getAnamnesis().toForm(), comando.getConsultation().toForm());

        redirectAttributes.addFlashAttribute("mensagem", "Consulta registrada com sucesso.");
        return "redirect:/pacientes/" + consulta.getPatient().getId() + "/historico";
    }

    // Retorno direto: usado quando a médica ja esta na ficha do paciente
    // (ver pacientes/historico.html) e nao precisa passar pela deteccao de
    // duplicidade de novo — o paciente ja esta identificado.
    @GetMapping("/retorno/{patientId}")
    public String formRetorno(@PathVariable Long patientId, Model model) {
        Patient paciente = patientService.buscarPorId(patientId);

        ConsultationFormCommand comando = new ConsultationFormCommand();
        comando.setData(LocalDate.now());
        comando.setValor(paciente.getValorConsultaPadrao());
        // Sugere CID/tratamento do ultimo retorno pra ela so ajustar em vez de
        // redigitar — continua editavel, nao trava o campo.
        consultationService.ultimaConsulta(patientId).ifPresent(ultima -> {
            comando.setCid(ultima.cid());
            comando.setTratamento(ultima.tratamento());
        });

        model.addAttribute("paciente", PatientSummary.from(paciente));
        model.addAttribute("comando", comando);
        return "consultas/retorno";
    }

    @PostMapping("/retorno/{patientId}")
    public String salvarRetorno(@PathVariable Long patientId,
                                 @Valid @ModelAttribute("comando") ConsultationFormCommand comando,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("paciente", PatientSummary.from(patientService.buscarPorId(patientId)));
            return "consultas/retorno";
        }

        consultationService.registrarRetorno(patientId, comando.toForm());

        redirectAttributes.addFlashAttribute("mensagem", "Consulta registrada com sucesso.");
        return "redirect:/pacientes/" + patientId + "/historico";
    }
}
