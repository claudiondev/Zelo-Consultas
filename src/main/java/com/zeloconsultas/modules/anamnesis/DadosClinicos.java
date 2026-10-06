package com.zeloconsultas.modules.anamnesis;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Dados clinicos gerais que fecham a secao 2 (HDA) do modelo original.
// Prefixo "clin_" pra nao colidir com colunas de mesmo nome em outros grupos
// da mesma tabela (ver comentario em ChecklistSintomas).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosClinicos {

    @Column(name = "clin_fumante")
    private Boolean fumante;

    @Column(name = "clin_etilista")
    private Boolean etilista;

    @Column(name = "clin_uso_spa")
    private Boolean usoSpa;

    @Column(name = "clin_altura_cm")
    private Integer alturaCm;

    @Column(name = "clin_peso_kg")
    private Double pesoKg;

    @Column(name = "clin_sinal_particularidade")
    private String sinalParticularidade;

    @Column(name = "clin_observacao")
    private String observacao;

    @Column(name = "clin_cirurgias")
    private String cirurgias;

    @Column(name = "clin_tratamento_medico")
    private String tratamentoMedico;

    // AFDM no modelo original
    @Column(name = "clin_antecedentes_familiares_doenca_mental")
    private String antecedentesFamiliaresDoencaMental;

    // A.FAM. no modelo original
    @Column(name = "clin_antecedentes_familiares")
    private String antecedentesFamiliares;

    @Column(name = "clin_dum_tpm")
    private String dumTpm;

    @Column(name = "clin_anticoncepcional")
    private String anticoncepcional;
}
