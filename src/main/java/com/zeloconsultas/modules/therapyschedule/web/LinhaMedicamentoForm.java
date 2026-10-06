package com.zeloconsultas.modules.therapyschedule.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Uma linha da tabela do esquema terapeutico: nome do medicamento + a dose em
// cada horario (texto livre, ex. "1 cp" — vazio quando nao se aplica aquele
// horario). Linhas sem nome preenchido sao descartadas antes de gerar o PDF
// (ver TherapyScheduleService) — a tela sempre tem algumas linhas em branco
// pra ela usar ou nao.
@Getter
@Setter
@NoArgsConstructor
public class LinhaMedicamentoForm {

    private String nomeMedicamento;
    private String manha;
    private String tarde;
    private String noite;
}
