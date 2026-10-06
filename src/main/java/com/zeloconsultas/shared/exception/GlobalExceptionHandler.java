package com.zeloconsultas.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Pagina de erro amigavel pra usuaria nao-tecnica — nunca deixa stack trace
// cru chegar na tela (nem por vazamento de informacao, nem por sustos).
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView tratarNaoEncontrado(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("erro");
        mav.addObject("mensagem", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    // Rota sem handler (ex.: url digitada errada, ou o h2-console quando esta
    // desligado) — sem isso, caia no catch-all abaixo e vira falso 500.
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView tratarRotaInexistente(NoResourceFoundException ex) {
        ModelAndView mav = new ModelAndView("erro");
        mav.addObject("mensagem", "Pagina nao encontrada.");
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView tratarErroInesperado(Exception ex) {
        // A tela esconde o detalhe da usuaria, mas o log tem que registrar o
        // erro inteiro — sem isso, um 500 vira caixa-preta na hora de depurar.
        log.error("Erro inesperado", ex);
        ModelAndView mav = new ModelAndView("erro");
        mav.addObject("mensagem", "Algo deu errado. Tente novamente ou avise o suporte.");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
