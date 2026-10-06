package com.zeloconsultas.modules.patient;

import com.zeloconsultas.modules.patient.dto.PatientForm;
import com.zeloconsultas.modules.patient.dto.PatientSummary;
import com.zeloconsultas.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientService(patientRepository);
    }

    @Test
    void buscarCandidatosDuplicidadeCombinaResultadosPorNomeEPorCpfSemRepetir() {
        Patient mesmoPacienteNosDoisFiltros = umPaciente(1L, "Maria Silva", "11144477735");

        when(patientRepository.findByNomeContainingIgnoreCase("Maria"))
                .thenReturn(List.of(mesmoPacienteNosDoisFiltros));
        when(patientRepository.findByCpf("11144477735"))
                .thenReturn(List.of(mesmoPacienteNosDoisFiltros));

        List<PatientSummary> candidatos = patientService.buscarCandidatosDuplicidade("Maria", "111.444.777-35");

        // O mesmo paciente apareceu nas duas buscas (nome e cpf) — nao pode duplicar na lista final.
        assertThat(candidatos).hasSize(1);
        assertThat(candidatos.get(0).nome()).isEqualTo("Maria Silva");
    }

    @Test
    void buscarCandidatosDuplicidadeNaoConsultaCpfQuandoNaoInformado() {
        when(patientRepository.findByNomeContainingIgnoreCase("Joao")).thenReturn(List.of());

        patientService.buscarCandidatosDuplicidade("Joao", null);

        verify(patientRepository, never()).findByCpf(anyString());
    }

    @Test
    void registrarNormalizaCpfAntesDeSalvar() {
        PatientForm form = new PatientForm("Maria Silva", "111.444.777-35", LocalDate.of(1990, 1, 1),
                'F', null, null, null, "83999990000", null, null, null, null, null);

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Patient salvo = patientService.registrar(form);

        assertThat(salvo.getCpf()).isEqualTo("11144477735");
        assertThat(salvo.getNome()).isEqualTo("Maria Silva");
    }

    @Test
    void registrarAceitaCpfNuloPorSerOpcional() {
        PatientForm form = new PatientForm("Sem CPF", null, LocalDate.of(2015, 5, 5),
                'M', null, null, null, "83999990000", null, null, null, null, null);

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Patient salvo = patientService.registrar(form);

        assertThat(salvo.getCpf()).isNull();
    }

    @Test
    void buscarPorIdLancaExcecaoQuandoNaoEncontrado() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private Patient umPaciente(Long id, String nome, String cpf) {
        return Patient.builder()
                .id(id)
                .nome(nome)
                .cpf(cpf)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .sexo('F')
                .telefone("83999990000")
                .build();
    }
}
