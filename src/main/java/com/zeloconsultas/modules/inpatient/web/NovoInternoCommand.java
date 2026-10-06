package com.zeloconsultas.modules.inpatient.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Tela /internos/novo junta cadastro + primeira anotacao, mesmo espirito de
// "Nova consulta" (paciente + primeira consulta juntos).
@Getter
@Setter
@NoArgsConstructor
public class NovoInternoCommand {

    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @NotNull(message = "Data de nascimento e obrigatoria")
    @Past(message = "Data de nascimento deve estar no passado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @NotNull(message = "Data da anotacao e obrigatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @NotBlank(message = "Anotacao e obrigatoria")
    private String anotacao;
}
