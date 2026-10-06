package com.zeloconsultas.modules.anamnesis;

import com.zeloconsultas.modules.patient.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// 1:1 com Patient, preenchida uma unica vez no primeiro atendimento (ver secoes
// 1 a 7 do modelo original). O que se repete a cada retorno e Consultation.
@Entity
@Table(name = "anamneses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Anamnesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    // Texto livre, potencialmente longo — CLOB em vez de VARCHAR limitado.
    @Lob
    @Column(name = "queixa_principal")
    private String queixaPrincipal;

    @Lob
    @Column(name = "historia_doenca_atual")
    private String historiaDoencaAtual;

    @Embedded
    private ChecklistSintomas checklistSintomas;

    @Embedded
    private DadosClinicos dadosClinicos;

    @Embedded
    private DadosCrianca dadosCrianca;

    @Embedded
    private DadosAdolescente dadosAdolescente;

    @Embedded
    private DadosAdulto dadosAdulto;

    @Lob
    @Column(name = "passado_psiquiatrico_morbido")
    private String passadoPsiquiatricoMorbido;

    @Embedded
    private HistoriaPsiquiatrica historiaPsiquiatrica;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Instant.now();
    }
}
