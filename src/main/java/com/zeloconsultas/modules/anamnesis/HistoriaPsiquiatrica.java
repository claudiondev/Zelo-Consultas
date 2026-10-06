package com.zeloconsultas.modules.anamnesis;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Secao 7 (H.Psiq) do modelo original.
// Prefixo "hpsiq_" pra nao colidir com colunas de mesmo nome em outros grupos
// da mesma tabela (ver comentario em ChecklistSintomas).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriaPsiquiatrica {

    @Column(name = "hpsiq_tratamento_medicamentoso")
    private String tratamentoMedicamentoso;

    @Column(name = "hpsiq_tempo")
    private String tempo;

    @Column(name = "hpsiq_adesao")
    private String adesao;

    @Column(name = "hpsiq_medico_responsavel")
    private String medicoResponsavel;

    @Column(name = "hpsiq_ect")
    private Boolean ect;

    @Column(name = "hpsiq_hospitalizacao")
    private String hospitalizacao;

    @Column(name = "hpsiq_exames")
    private String exames;
}
