package com.zeloconsultas.modules.patient.dto;

import com.zeloconsultas.modules.patient.Patient;

import java.time.LocalDate;

// Projecao enxuta pra telas de busca/confirmacao — evita expor a entidade JPA
// inteira (e o mapeamento) direto pro Controller/Thymeleaf.
public record PatientSummary(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNascimento
) {
    public static PatientSummary from(Patient patient) {
        return new PatientSummary(
                patient.getId(),
                patient.getNome(),
                patient.getCpf(),
                patient.getDataNascimento()
        );
    }
}
