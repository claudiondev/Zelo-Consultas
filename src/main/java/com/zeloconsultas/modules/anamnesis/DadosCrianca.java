package com.zeloconsultas.modules.anamnesis;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Secao 3 do modelo original — so preenchida quando o paciente e crianca.
// Prefixo "crianca_" pra nao colidir com colunas de mesmo nome em outros
// grupos da mesma tabela (ver comentario em ChecklistSintomas).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosCrianca {

    @Column(name = "crianca_alimentacao")
    private String alimentacao;

    @Column(name = "crianca_marcha")
    private String marcha;

    @Column(name = "crianca_linguagem")
    private String linguagem;

    @Column(name = "crianca_sono")
    private String sono;

    @Column(name = "crianca_esfincteres")
    private String esfincteres;

    @Column(name = "crianca_tiques")
    private String tiques;

    @Column(name = "crianca_escolar")
    private String escolar;

    @Column(name = "crianca_social")
    private String social;

    @Column(name = "crianca_dpi")
    private String dpi;

    @Column(name = "crianca_hospitalizacao")
    private String hospitalizacao;

    @Column(name = "crianca_vacinas")
    private String vacinas;

    @Column(name = "crianca_uso_celular")
    private String usoCelular;
}
