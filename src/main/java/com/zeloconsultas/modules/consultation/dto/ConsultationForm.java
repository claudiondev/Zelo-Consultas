package com.zeloconsultas.modules.consultation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

// Usado tanto na primeira consulta (junto com PatientForm + AnamnesisForm)
// quanto no retorno (sozinho, anexado a um paciente ja existente).
public record ConsultationForm(

        @NotNull(message = "Data da consulta e obrigatoria")
        LocalDate data,

        @NotBlank(message = "Historico e obrigatorio")
        String historico,

        @NotBlank(message = "Tratamento e obrigatorio")
        String tratamento,

        String obs,

        String cid,

        @Min(value = 0, message = "Dias de atestado nao pode ser negativo")
        Integer atestadoDias,

        String recomendacao,

        String riscoSuicidio,

        BigDecimal valor,

        String retornarEm
) {
}
