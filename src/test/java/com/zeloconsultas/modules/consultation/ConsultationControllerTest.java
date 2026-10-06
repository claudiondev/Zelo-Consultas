package com.zeloconsultas.modules.consultation;

import com.zeloconsultas.modules.consultation.dto.ConsultationForm;
import com.zeloconsultas.modules.consultation.web.NovaConsultaCommand;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.modules.patient.dto.PatientForm;
import com.zeloconsultas.modules.patient.dto.PatientSummary;
import com.zeloconsultas.shared.backup.PendriveBackupService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Cobre a confirmacao de duplicidade — em especial o caso do
// pacienteExistenteId adulterado (ver revisao de seguranca de 2026-07-13):
// o valor vem de um campo oculto no POST e nao pode ser confiado sem
// reconferir contra os candidatos reais da busca.
@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

    @Mock
    private ConsultationService consultationService;
    @Mock
    private PatientService patientService;
    @Mock
    private PendriveBackupService pendriveBackupService;
    @Mock
    private HttpSession session;

    private ConsultationController controller;

    @BeforeEach
    void setUp() {
        controller = new ConsultationController(consultationService, patientService, pendriveBackupService);
    }

    @Test
    void confirmarDuplicidade_semComandoNaSessao_recomecaDoZero() {
        when(session.getAttribute("novaConsultaPendente")).thenReturn(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String destino = controller.confirmarDuplicidade(9L, session, redirect);

        assertThat(destino).isEqualTo("redirect:/consultas/nova");
        verifyNoInteractions(consultationService);
    }

    @Test
    void confirmarDuplicidade_idPertenceAosCandidatos_registraRetorno() {
        NovaConsultaCommand comando = umComando();
        when(session.getAttribute("novaConsultaPendente")).thenReturn(comando);
        when(patientService.buscarCandidatosDuplicidade("Maria Silva", null))
                .thenReturn(List.of(new PatientSummary(7L, "Maria Silva", null, LocalDate.of(1990, 1, 1))));

        Patient pacienteExistente = Patient.builder().id(7L).nome("Maria Silva").build();
        when(consultationService.registrarRetorno(eq(7L), any(ConsultationForm.class)))
                .thenReturn(Consultation.builder().patient(pacienteExistente).build());

        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        String destino = controller.confirmarDuplicidade(7L, session, redirect);

        verify(consultationService).registrarRetorno(eq(7L), any(ConsultationForm.class));
        verify(consultationService, never()).registrarPrimeiraConsulta(any(), any(), any());
        verify(session).removeAttribute("novaConsultaPendente");
        assertThat(destino).isEqualTo("redirect:/pacientes/7/historico");
    }

    @Test
    void confirmarDuplicidade_idNaoPertenceAosCandidatos_naoRegistraEmFichaAlheia() {
        NovaConsultaCommand comando = umComando();
        when(session.getAttribute("novaConsultaPendente")).thenReturn(comando);
        // Busca real so devolve a paciente 7 — 999 e um id de outra pessoa
        // qualquer, forjado no campo oculto do formulario.
        when(patientService.buscarCandidatosDuplicidade("Maria Silva", null))
                .thenReturn(List.of(new PatientSummary(7L, "Maria Silva", null, LocalDate.of(1990, 1, 1))));

        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        String destino = controller.confirmarDuplicidade(999L, session, redirect);

        verify(consultationService, never()).registrarRetorno(any(), any());
        verify(consultationService, never()).registrarPrimeiraConsulta(any(), any(), any());
        verify(session, never()).removeAttribute(anyString());
        assertThat(destino).isEqualTo("redirect:/consultas/nova");
        assertThat(redirect.getFlashAttributes()).containsKey("mensagem");
    }

    @Test
    void confirmarDuplicidade_semIdExistente_registraPacienteNovo() {
        NovaConsultaCommand comando = umComando();
        when(session.getAttribute("novaConsultaPendente")).thenReturn(comando);

        Patient pacienteNovo = Patient.builder().id(15L).nome("Maria Silva").build();
        when(consultationService.registrarPrimeiraConsulta(any(PatientForm.class), any(), any()))
                .thenReturn(Consultation.builder().patient(pacienteNovo).build());

        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        String destino = controller.confirmarDuplicidade(null, session, redirect);

        // Sem id, nao ha por que reconferir candidatos — vai direto pro cadastro novo.
        verify(patientService, never()).buscarCandidatosDuplicidade(any(), any());
        verify(consultationService).registrarPrimeiraConsulta(any(PatientForm.class), any(), any());
        assertThat(destino).isEqualTo("redirect:/pacientes/15/historico");
    }

    private NovaConsultaCommand umComando() {
        NovaConsultaCommand comando = new NovaConsultaCommand();
        comando.getPatient().setNome("Maria Silva");
        comando.getPatient().setDataNascimento(LocalDate.of(1990, 1, 1));
        comando.getPatient().setSexo('F');
        comando.getPatient().setTelefone("83999990000");
        comando.getConsultation().setData(LocalDate.now());
        comando.getConsultation().setHistorico("Historico de teste");
        comando.getConsultation().setTratamento("Tratamento de teste");
        return comando;
    }
}
