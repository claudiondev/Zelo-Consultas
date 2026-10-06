package com.zeloconsultas.modules.anamnesis.dto;

public record DadosClinicosForm(
        Boolean fumante,
        Boolean etilista,
        Boolean usoSpa,
        Integer alturaCm,
        Double pesoKg,
        String sinalParticularidade,
        String observacao,
        String cirurgias,
        String tratamentoMedico,
        String antecedentesFamiliaresDoencaMental,
        String antecedentesFamiliares,
        String dumTpm,
        String anticoncepcional
) {
}
