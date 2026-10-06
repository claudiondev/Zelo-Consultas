package com.zeloconsultas.modules.inpatient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

// Cadastro enxuto pra paciente internado que a médica acompanha na
// clinica — de proposito sem CPF/telefone/endereco/anamnese (ver spec em
// docs/superpowers/specs/2026-07-09-modulo-internos-design.md). Sem deteccao
// de duplicidade tambem: com so nome+nascimento o recurso ajudaria pouco.
@Entity
@Table(name = "inpatients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inpatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Instant.now();
    }
}
