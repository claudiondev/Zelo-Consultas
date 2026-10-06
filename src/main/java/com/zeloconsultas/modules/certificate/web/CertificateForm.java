package com.zeloconsultas.modules.certificate.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Os dois campos editaveis antes de gerar o atestado (confirmado com Claudio):
// o texto do corpo (ja pre-preenchido pelo service) e a data.
@Getter
@Setter
@NoArgsConstructor
public class CertificateForm {

    @NotBlank(message = "O texto do atestado nao pode ficar vazio")
    private String texto;

    // Formato explicito: sem isso, o Thymeleaf reexibe em dd/MM/yyyy, que o
    // <input type="date"> nao reconhece e mostra como vazio.
    @NotNull(message = "Informe a data do atestado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
}
