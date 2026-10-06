package com.zeloconsultas.modules.consultation.web;

import com.zeloconsultas.modules.consultation.dto.ConsultationForm;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ConsultationFormCommand implements Serializable {

    // Formato explicito: sem isso, o Thymeleaf reexibe o valor em dd/MM/yyyy,
    // que o <input type="date"> nao reconhece e mostra como vazio.
    @NotNull(message = "Data da consulta e obrigatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @NotBlank(message = "Historico e obrigatorio")
    private String historico;

    @NotBlank(message = "Tratamento e obrigatorio")
    private String tratamento;

    private String obs;
    private String cid;

    @Min(value = 0, message = "Dias de atestado nao pode ser negativo")
    private Integer atestadoDias;

    private String recomendacao;
    private String riscoSuicidio;
    private BigDecimal valor;
    private String retornarEm;

    public ConsultationForm toForm() {
        return new ConsultationForm(data, historico, tratamento, obs, cid, atestadoDias,
                recomendacao, riscoSuicidio, valor, retornarEm);
    }
}
