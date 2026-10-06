package com.zeloconsultas.modules.consultation.web;

import com.zeloconsultas.modules.anamnesis.web.AnamnesisFormCommand;
import com.zeloconsultas.modules.patient.web.PatientFormCommand;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

// Formulario unico da tela "Nova Consulta" (ver plano: dados do paciente +
// anamnese + primeira evolucao juntos, um botao Salvar). Implementa
// Serializable porque fica guardado na HttpSession entre o POST inicial e a
// confirmacao de duplicidade (ver ConsultationController).
@Getter
@Setter
@NoArgsConstructor
public class NovaConsultaCommand implements Serializable {

    @Valid
    private PatientFormCommand patient = new PatientFormCommand();

    @Valid
    private AnamnesisFormCommand anamnesis = new AnamnesisFormCommand();

    @Valid
    private ConsultationFormCommand consultation = new ConsultationFormCommand();
}
