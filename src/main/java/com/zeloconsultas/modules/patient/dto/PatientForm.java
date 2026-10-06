package com.zeloconsultas.modules.patient.dto;

import com.zeloconsultas.shared.validation.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDate;

// Dados de cabecalho da ficha medica. CPF fica opcional de proposito (ver
// modelagem do prontuario: nem todo paciente real tem CPF em maos).
public record PatientForm(

        @NotBlank(message = "Nome e obrigatorio")
        String nome,

        @Cpf
        String cpf,

        @NotNull(message = "Data de nascimento e obrigatoria")
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @NotNull(message = "Sexo e obrigatorio")
        Character sexo,

        String planoSaude,

        String endereco,

        String localAtendimento,

        @NotBlank(message = "Telefone e obrigatorio")
        String telefone,

        String religiao,

        String profissao,

        String acompanhante,

        BigDecimal valorConsultaPadrao,

        String reciboNf
) {
}
