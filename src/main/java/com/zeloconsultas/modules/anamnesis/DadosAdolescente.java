package com.zeloconsultas.modules.anamnesis;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Secao 4 do modelo original — so preenchida quando o paciente e adolescente.
// Prefixo "adolesc_" pra nao colidir com colunas de mesmo nome em outros
// grupos da mesma tabela (ver comentario em ChecklistSintomas).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosAdolescente {

    @Column(name = "adolesc_socializacao")
    private String socializacao;

    @Column(name = "adolesc_namoro_sexo")
    private String namoroSexo;

    @Column(name = "adolesc_drogas")
    private String drogas;

    @Column(name = "adolesc_problemas_legais")
    private String problemasLegais;

    @Column(name = "adolesc_observacao")
    private String observacao;
}
