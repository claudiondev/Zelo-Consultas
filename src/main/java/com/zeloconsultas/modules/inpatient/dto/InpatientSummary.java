package com.zeloconsultas.modules.inpatient.dto;

import com.zeloconsultas.modules.inpatient.Inpatient;

import java.time.LocalDate;

// Projecao enxuta pra tela de lista de internos.
public record InpatientSummary(
        Long id,
        String nome,
        LocalDate dataNascimento
) {
    public static InpatientSummary from(Inpatient inpatient) {
        return new InpatientSummary(inpatient.getId(), inpatient.getNome(), inpatient.getDataNascimento());
    }
}
