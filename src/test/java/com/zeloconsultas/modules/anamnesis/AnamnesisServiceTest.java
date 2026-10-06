package com.zeloconsultas.modules.anamnesis;

import com.zeloconsultas.modules.anamnesis.dto.*;
import com.zeloconsultas.modules.patient.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnamnesisServiceTest {

    @Mock
    private AnamnesisRepository anamnesisRepository;

    private AnamnesisService anamnesisService;

    @BeforeEach
    void setUp() {
        anamnesisService = new AnamnesisService(anamnesisRepository);
    }

    @Test
    void registrarMapeiaCamposObrigatoriosEChecklist() {
        Patient patient = Patient.builder().id(1L).nome("Maria Silva").build();
        when(anamnesisRepository.save(any(Anamnesis.class))).thenAnswer(inv -> inv.getArgument(0));

        AnamnesisForm form = new AnamnesisForm(
                "  Queixa principal  ",
                "  Historia da doenca atual  ",
                new ChecklistSintomasForm(true, false, null, true, null, null, null, null, null, null, null, null, null, null, null, null),
                new DadosClinicosForm(true, false, null, 170, 65.5, null, null, null, null, null, null, null, null),
                null, null, null,
                null,
                null
        );

        Anamnesis salvo = anamnesisService.registrar(patient, form);

        // trim() aplicado nos campos de texto livre obrigatorios
        assertThat(salvo.getQueixaPrincipal()).isEqualTo("Queixa principal");
        assertThat(salvo.getHistoriaDoencaAtual()).isEqualTo("Historia da doenca atual");
        assertThat(salvo.getPatient()).isEqualTo(patient);

        assertThat(salvo.getChecklistSintomas().getHumor()).isTrue();
        assertThat(salvo.getChecklistSintomas().getCulpa()).isFalse();
        assertThat(salvo.getChecklistSintomas().getSuicidio()).isNull();

        assertThat(salvo.getDadosClinicos().getAlturaCm()).isEqualTo(170);
        assertThat(salvo.getDadosClinicos().getPesoKg()).isEqualTo(65.5);

        // Grupos opcionais (crianca/adolescente/adulto) nao preenchidos no formulario
        // devem ficar nulos na entidade, nao um objeto com todos os campos vazios.
        assertThat(salvo.getDadosCrianca()).isNull();
        assertThat(salvo.getDadosAdolescente()).isNull();
        assertThat(salvo.getDadosAdulto()).isNull();
        assertThat(salvo.getHistoriaPsiquiatrica()).isNull();
    }

    @Test
    void registrarConvertePassadoPsiquiatricoMorbidoEmBrancoParaNulo() {
        Patient patient = Patient.builder().id(1L).build();
        when(anamnesisRepository.save(any(Anamnesis.class))).thenAnswer(inv -> inv.getArgument(0));

        AnamnesisForm form = new AnamnesisForm(
                "QP", "HDA",
                new ChecklistSintomasForm(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                new DadosClinicosForm(null, null, null, null, null, null, null, null, null, null, null, null, null),
                null, null, null,
                "   ",
                null
        );

        Anamnesis salvo = anamnesisService.registrar(patient, form);

        assertThat(salvo.getPassadoPsiquiatricoMorbido()).isNull();
    }
}
