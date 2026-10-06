package com.zeloconsultas.modules.patient.web;

import com.zeloconsultas.modules.patient.dto.PatientForm;
import com.zeloconsultas.shared.validation.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

// Bean mutavel so pra binding do Thymeleaf (th:field precisa de getter/setter
// classicos). PatientForm (record, imutavel) e o contrato real do Service —
// toForm() faz a ponte entre os dois. Serializable porque fica na HttpSession
// durante o fluxo de confirmacao de duplicidade (ver ConsultationController).
@Getter
@Setter
@NoArgsConstructor
public class PatientFormCommand implements Serializable {

    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @Cpf
    private String cpf;

    @NotNull(message = "Data de nascimento e obrigatoria")
    @Past(message = "Data de nascimento deve estar no passado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @NotNull(message = "Sexo e obrigatorio")
    private Character sexo;

    private String planoSaude;
    private String endereco;
    private String localAtendimento;

    @NotBlank(message = "Telefone e obrigatorio")
    private String telefone;

    private String religiao;
    private String profissao;
    private String acompanhante;
    private BigDecimal valorConsultaPadrao;
    private String reciboNf;

    public PatientForm toForm() {
        return new PatientForm(nome, cpf, dataNascimento, sexo, planoSaude, endereco,
                localAtendimento, telefone, religiao, profissao, acompanhante, valorConsultaPadrao, reciboNf);
    }
}
