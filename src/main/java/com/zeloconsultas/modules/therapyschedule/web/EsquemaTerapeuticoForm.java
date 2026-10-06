package com.zeloconsultas.modules.therapyschedule.web;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Lista dinamica de linhas (Spring faz bind por indice: linhas[0].nomeMedicamento,
// linhas[1].nomeMedicamento, etc — precisa so do getter/setter de uma List
// mutavel, o WebDataBinder cresce a lista sozinho). O JS de tela
// (esquema-terapeutico.js) adiciona/remove linhas e renumera os indices.
@Getter
@Setter
@NoArgsConstructor
public class EsquemaTerapeuticoForm {

    private List<LinhaMedicamentoForm> linhas = new ArrayList<>();

    @NotNull(message = "Informe a data")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
}
