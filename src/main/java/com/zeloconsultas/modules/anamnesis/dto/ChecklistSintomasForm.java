package com.zeloconsultas.modules.anamnesis.dto;

// Espelha ChecklistSintomas (entity) — Boolean, nao boolean, pra distinguir
// "nao avaliado" (null, checkbox nunca marcado) de "avaliado e ausente" (false).
public record ChecklistSintomasForm(
        Boolean humor,
        Boolean culpa,
        Boolean suicidio,
        Boolean insonia,
        Boolean pensamento,
        Boolean agitacao,
        Boolean ansiedadeTensao,
        Boolean tiques,
        Boolean toc,
        Boolean dificuldadesIntelectuais,
        Boolean sintomasDigestorios,
        Boolean sintomasMuscular,
        Boolean sintomasGenitourinarios,
        Boolean hipocondria,
        Boolean criticaDaDoenca,
        Boolean incapacidadeDeSentir
) {
}
