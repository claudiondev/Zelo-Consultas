package com.zeloconsultas.modules.consultation;

import com.zeloconsultas.modules.patient.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

// N:1 com Patient — uma linha por retorno. E o bloco (secao 8 do modelo
// original) que se repete ~20x e literalmente "esgota" o Word fisico.
@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDate data;

    @Lob
    @Column(nullable = false)
    private String historico;

    @Lob
    @Column(nullable = false)
    private String tratamento;

    @Lob
    private String obs;

    // CID(s) do modelo real vem combinados (ex.: "F32 + F41.8"), entao nao e um
    // codigo unico curto — folga suficiente pra listar mais de um. Nao validado
    // contra tabela oficial no v1 (fora de escopo, ver CLAUDE.md).
    @Column(length = 100)
    private String cid;

    @Column(name = "atestado_dias")
    private Integer atestadoDias;

    private String recomendacao;

    // Texto livre de proposito: o modelo original nao define uma lista fechada
    // de niveis de risco, entao nao travamos com enum sem confirmar com a medica.
    @Column(name = "risco_suicidio")
    private String riscoSuicidio;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    // "data/texto" no modelo original (ex.: "30 dias", "retorno se necessario") —
    // por isso String, nao LocalDate.
    @Column(name = "retornar_em")
    private String retornarEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Instant.now();
    }
}
