package com.zeloconsultas.modules.anamnesis;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// "Cs durante entrevista" do modelo original — checklist marcado na hora da consulta.
// Boolean (objeto) em vez de boolean primitivo de proposito: null = "nao avaliado",
// diferente de false = "avaliado e ausente". Perder essa distincao seria perder
// informacao clinica real.
//
// Todas as colunas com prefixo "sint_" de proposito: varios @Embeddable desta
// entidade compartilham a mesma tabela, e nomes genericos (ex.: "tiques",
// "observacao") colidem entre grupos diferentes se nao forem prefixados.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistSintomas {

    @Column(name = "sint_humor")
    private Boolean humor;

    @Column(name = "sint_culpa")
    private Boolean culpa;

    @Column(name = "sint_suicidio")
    private Boolean suicidio;

    @Column(name = "sint_insonia")
    private Boolean insonia;

    @Column(name = "sint_pensamento")
    private Boolean pensamento;

    @Column(name = "sint_agitacao")
    private Boolean agitacao;

    @Column(name = "sint_ansiedade_tensao")
    private Boolean ansiedadeTensao;

    @Column(name = "sint_tiques")
    private Boolean tiques;

    @Column(name = "sint_toc")
    private Boolean toc;

    @Column(name = "sint_dificuldades_intelectuais")
    private Boolean dificuldadesIntelectuais;

    @Column(name = "sint_sintomas_digestorios")
    private Boolean sintomasDigestorios;

    @Column(name = "sint_sintomas_muscular")
    private Boolean sintomasMuscular;

    @Column(name = "sint_sintomas_genitourinarios")
    private Boolean sintomasGenitourinarios;

    @Column(name = "sint_hipocondria")
    private Boolean hipocondria;

    @Column(name = "sint_critica_da_doenca")
    private Boolean criticaDaDoenca;

    @Column(name = "sint_incapacidade_de_sentir")
    private Boolean incapacidadeDeSentir;
}
