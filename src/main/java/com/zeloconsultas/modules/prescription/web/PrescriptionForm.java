package com.zeloconsultas.modules.prescription.web;

import com.zeloconsultas.shared.validation.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Os campos editaveis antes de gerar o receituario (mesmo padrao do
// CertificateForm): via de uso, texto livre da prescricao e data. CPF e
// endereco sao obrigatorios AQUI mesmo que o cadastro do paciente/interno nao
// tenha (ver decisao de tornar CPF/endereco obrigatorios so pro receituario,
// nao pro cadastro em geral) — pre-preenchidos quando o paciente ja tem, e
// digitados na hora quando nao tem; nunca sao salvos de volta no cadastro.
@Getter
@Setter
@NoArgsConstructor
public class PrescriptionForm {

    @NotBlank(message = "Informe a via de uso")
    private String viaUso = "USO ORAL";

    @NotBlank(message = "Informe o CPF do paciente")
    @Cpf
    private String cpf;

    @NotBlank(message = "Informe o endereco do paciente")
    private String endereco;

    @NotBlank(message = "A prescricao nao pode ficar vazia")
    private String prescricao;

    @NotNull(message = "Informe a data do receituario")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
}
