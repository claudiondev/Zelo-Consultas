package com.zeloconsultas.config;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String pagina(@RequestParam(required = false) String error,
                          @RequestParam(required = false) String saiu,
                          Model model) {
        model.addAttribute("erro", error != null);
        model.addAttribute("saiu", saiu != null);
        return "login";
    }
}
