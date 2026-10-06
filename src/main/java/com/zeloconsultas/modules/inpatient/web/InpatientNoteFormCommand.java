package com.zeloconsultas.modules.inpatient.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Formulario inline na ficha do interno (GET /internos/{id}) pra registrar
// uma nova anotacao sem trocar de tela.
@Getter
@Setter
@NoArgsConstructor
public class InpatientNoteFormCommand {

    @NotNull(message = "Data da anotacao e obrigatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @NotBlank(message = "Anotacao e obrigatoria")
    private String anotacao;
}
