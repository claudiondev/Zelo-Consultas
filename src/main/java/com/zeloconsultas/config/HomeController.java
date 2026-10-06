package com.zeloconsultas.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Tela de escolha entre os dois fluxos do sistema: Consultas (ambulatorio)
// e Internos (pacientes internados que ela acompanha). O painel que antes
// vivia aqui mudou de endereco pra /consultas (ver ConsultationController).
@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
