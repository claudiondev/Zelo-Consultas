package com.zeloconsultas.modules.inpatient.dto;

import com.zeloconsultas.modules.inpatient.InpatientNote;

import java.time.LocalDate;

// Projecao pra lista de anotacoes na ficha do interno.
public record InpatientNoteSummary(
        Long id,
        LocalDate data,
        String anotacao
) {
    public static InpatientNoteSummary from(InpatientNote note) {
        return new InpatientNoteSummary(note.getId(), note.getData(), note.getAnotacao());
    }
}
