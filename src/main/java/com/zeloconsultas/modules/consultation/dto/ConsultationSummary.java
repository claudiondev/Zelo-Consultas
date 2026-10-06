package com.zeloconsultas.modules.consultation.dto;

import com.zeloconsultas.modules.consultation.Consultation;

import java.math.BigDecimal;
import java.time.LocalDate;

// Projecao pra tela de historico do paciente.
public record ConsultationSummary(
        Long id,
        LocalDate data,
        String historico,
        String tratamento,
        String cid,
        Integer atestadoDias,
        BigDecimal valor
) {
    public static ConsultationSummary from(Consultation consultation) {
        return new ConsultationSummary(
                consultation.getId(),
                consultation.getData(),
                consultation.getHistorico(),
                consultation.getTratamento(),
                consultation.getCid(),
                consultation.getAtestadoDias(),
                consultation.getValor()
        );
    }
}
