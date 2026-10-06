package com.zeloconsultas.modules.anamnesis;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Secao 5 do modelo original — laboral / social / familia do paciente adulto.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosAdulto {

    @Column(name = "laboral_forma")
    private String laboralForma;

    @Column(name = "laboral_funcao")
    private String laboralFuncao;

    @Column(name = "laboral_adequacao")
    private String laboralAdequacao;

    @Column(name = "social_amizade")
    private String socialAmizade;

    @Column(name = "social_habitos")
    private String socialHabitos;

    @Column(name = "social_lazer")
    private String socialLazer;

    @Column(name = "familia_conjuge")
    private String familiaConjuge;

    @Column(name = "familia_mora_com")
    private String familiaMoraCom;
}
