package com.zeloconsultas.modules.consultation;

import com.zeloconsultas.modules.anamnesis.AnamnesisService;
import com.zeloconsultas.modules.anamnesis.dto.AnamnesisForm;
import com.zeloconsultas.modules.consultation.dto.AtendimentoRecente;
import com.zeloconsultas.modules.consultation.dto.ConsultationForm;
import com.zeloconsultas.modules.consultation.dto.ConsultationSummary;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.modules.patient.dto.PatientForm;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientService patientService;
    private final AnamnesisService anamnesisService;
    private final ApplicationEventPublisher eventPublisher;

    public ConsultationService(ConsultationRepository consultationRepository,
                                PatientService patientService,
                                AnamnesisService anamnesisService,
                                ApplicationEventPublisher eventPublisher) {
        this.consultationRepository = consultationRepository;
        this.patientService = patientService;
        this.anamnesisService = anamnesisService;
        this.eventPublisher = eventPublisher;
    }

    // Primeiro atendimento: cria paciente + anamnese + a primeira entrada de
    // consulta juntos, na mesma transacao — ou salva tudo, ou nao salva nada.
    @Transactional
    public Consultation registrarPrimeiraConsulta(PatientForm patientForm,
                                                    AnamnesisForm anamnesisForm,
                                                    ConsultationForm consultationForm) {
        Patient patient = patientService.registrar(patientForm);
        anamnesisService.registrar(patient, anamnesisForm);
        return salvarConsulta(patient, consultationForm);
    }

    // Retorno: paciente ja existe (confirmado pela medica na tela de
    // duplicidade), so entra uma evolucao nova.
    @Transactional
    public Consultation registrarRetorno(Long patientId, ConsultationForm consultationForm) {
        Patient patient = patientService.buscarPorId(patientId);
        return salvarConsulta(patient, consultationForm);
    }

    public List<ConsultationSummary> historicoDoPaciente(Long patientId) {
        return consultationRepository.findByPatientIdOrderByDataDesc(patientId).stream()
                .map(ConsultationSummary::from)
                .collect(Collectors.toList());
    }

    // Ultimos atendimentos de qualquer paciente, pra home nao ficar so com botoes soltos.
    public List<AtendimentoRecente> atendimentosRecentes() {
        return consultationRepository.findTop5ByOrderByDataDescIdDesc().stream()
                .map(AtendimentoRecente::from)
                .collect(Collectors.toList());
    }

    // Ultima consulta do paciente, pra sugerir CID/tratamento no formulario de retorno.
    public Optional<ConsultationSummary> ultimaConsulta(Long patientId) {
        return consultationRepository.findFirstByPatientIdOrderByDataDesc(patientId)
                .map(ConsultationSummary::from);
    }

    private Consultation salvarConsulta(Patient patient, ConsultationForm form) {
        Consultation consultation = Consultation.builder()
                .patient(patient)
                .data(form.data())
                .historico(form.historico().trim())
                .tratamento(form.tratamento().trim())
                .obs(blankToNull(form.obs()))
                .cid(blankToNull(form.cid()))
                .atestadoDias(form.atestadoDias())
                .recomendacao(blankToNull(form.recomendacao()))
                .riscoSuicidio(blankToNull(form.riscoSuicidio()))
                .valor(form.valor())
                .retornarEm(blankToNull(form.retornarEm()))
                .build();

        Consultation salva = consultationRepository.save(consultation);

        // Dispara o backup — mas so depois do commit (ver BackupService), pra
        // copiar dados ja gravados. Publicar aqui e seguro mesmo dentro da tx.
        eventPublisher.publishEvent(new ConsultationSavedEvent(salva.getId()));
        return salva;
    }

    private String blankToNull(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
