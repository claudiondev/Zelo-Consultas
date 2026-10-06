package com.zeloconsultas.modules.anamnesis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Preenchido uma unica vez, no primeiro atendimento (ver Anamnesis entity).
// So QP e HDA sao obrigatorios — o resto do formulario e clinico/livre e a
// medica preenche so o que for pertinente ao caso (nem todo paciente e crianca
// ou adolescente, por exemplo).
public record AnamnesisForm(

        @NotBlank(message = "Queixa principal e obrigatoria")
        String queixaPrincipal,

        @NotBlank(message = "Historia da doenca atual e obrigatoria")
        String historiaDoencaAtual,

        @NotNull
        ChecklistSintomasForm checklistSintomas,

        @NotNull
        DadosClinicosForm dadosClinicos,

        DadosCriancaForm dadosCrianca,

        DadosAdolescenteForm dadosAdolescente,

        DadosAdultoForm dadosAdulto,

        String passadoPsiquiatricoMorbido,

        HistoriaPsiquiatricaForm historiaPsiquiatrica
) {
}
