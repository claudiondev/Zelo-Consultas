package com.zeloconsultas.modules.consultation.dto;

import com.zeloconsultas.modules.consultation.Consultation;

import java.time.LocalDate;

// Projecao enxuta pra lista de "ultimos atendimentos" na home.
public record AtendimentoRecente(
        Long patientId,
        String patientNome,
        LocalDate data
) {
    public static AtendimentoRecente from(Consultation consultation) {
        return new AtendimentoRecente(
                consultation.getPatient().getId(),
                consultation.getPatient().getNome(),
                consultation.getData()
        );
    }
}
