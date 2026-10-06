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

// Anotacao de rotina de um interno — N:1 com Inpatient, uma linha por
// observacao. So data + texto livre, bem mais enxuto que Consultation
// (sem CID/tratamento/valor/atestado — nao se aplica aqui).
@Entity
@Table(name = "inpatient_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InpatientNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inpatient_id", nullable = false)
    private Inpatient inpatient;

    @Column(nullable = false)
    private LocalDate data;

    @Lob
    @Column(nullable = false)
    private String anotacao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Instant.now();
    }
}
