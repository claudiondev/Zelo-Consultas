package com.zeloconsultas.modules.anamnesis.dto;

public record HistoriaPsiquiatricaForm(
        String tratamentoMedicamentoso,
        String tempo,
        String adesao,
        String medicoResponsavel,
        Boolean ect,
        String hospitalizacao,
        String exames
) {
}
