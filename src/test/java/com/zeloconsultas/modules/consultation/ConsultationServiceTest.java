package com.zeloconsultas.modules.consultation;

import com.zeloconsultas.modules.anamnesis.AnamnesisService;
import com.zeloconsultas.modules.anamnesis.dto.*;
import com.zeloconsultas.modules.consultation.dto.ConsultationForm;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.modules.patient.dto.PatientForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private PatientService patientService;
    @Mock
    private AnamnesisService anamnesisService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ConsultationService consultationService;

    @BeforeEach
    void setUp() {
        consultationService = new ConsultationService(
                consultationRepository, patientService, anamnesisService, eventPublisher);
    }

    @Test
    void primeiraConsultaCriaPacienteDepoisAnamneseDepoisConsulta() {
        Patient pacienteNovo = Patient.builder().id(1L).nome("Maria Silva").build();
        when(patientService.registrar(any(PatientForm.class))).thenReturn(pacienteNovo);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(inv -> inv.getArgument(0));

        Consultation resultado = consultationService.registrarPrimeiraConsulta(
                umPatientForm(), umAnamnesisForm(), umConsultationForm());

        // Ordem importa: anamnese so pode ser vinculada a um paciente que ja existe.
        InOrder ordem = inOrder(patientService, anamnesisService, consultationRepository);
        ordem.verify(patientService).registrar(any(PatientForm.class));
        ordem.verify(anamnesisService).registrar(eq(pacienteNovo), any(AnamnesisForm.class));
        ordem.verify(consultationRepository).save(any(Consultation.class));

        assertThat(resultado.getPatient()).isEqualTo(pacienteNovo);
        // Dispara o evento que aciona o backup automatico.
        verify(eventPublisher).publishEvent(any(ConsultationSavedEvent.class));
    }

    @Test
    void retornoNaoCriaPacienteNemAnamneseDeNovo() {
        Patient pacienteExistente = Patient.builder().id(7L).nome("Joao Souza").build();
        when(patientService.buscarPorId(7L)).thenReturn(pacienteExistente);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(inv -> inv.getArgument(0));

        Consultation resultado = consultationService.registrarRetorno(7L, umConsultationForm());

        verify(patientService, never()).registrar(any());
        verify(anamnesisService, never()).registrar(any(), any());
        assertThat(resultado.getPatient()).isEqualTo(pacienteExistente);
        verify(eventPublisher).publishEvent(any(ConsultationSavedEvent.class));
    }

    private PatientForm umPatientForm() {
        return new PatientForm("Maria Silva", null, LocalDate.of(1990, 1, 1), 'F',
                null, null, null, "83999990000", null, null, null, null, null);
    }

    private AnamnesisForm umAnamnesisForm() {
        return new AnamnesisForm("Queixa principal de teste", "Historia da doenca atual de teste",
                new ChecklistSintomasForm(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                new DadosClinicosForm(null, null, null, null, null, null, null, null, null, null, null, null, null),
                null, null, null, null, null);
    }

    private ConsultationForm umConsultationForm() {
        return new ConsultationForm(LocalDate.now(), "Historico de teste", "Tratamento de teste",
                null, null, null, null, null, null, null);
    }
}
