package com.zeloconsultas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Trava de PIN pedida explicitamente: usuario/senha fixos (ver
// ProntuarioProperties.Login), sem cadastro de conta — e um app local de uso
// unico. O CSRF do Spring Security vem ligado por padrao, e o
// thymeleaf-extras-springsecurity6 injeta o token automaticamente em todo
// <form th:action="..."> do projeto, sem precisar mexer nos templates.
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(ProntuarioProperties properties, PasswordEncoder encoder) {
        String senha = properties.getLogin().getSenha();
        if (senha == null || senha.isBlank()) {
            // Falha rapido e claro em vez de codificar uma senha vazia (que
            // faria QUALQUER tentativa com campo em branco logar com sucesso).
            // A senha nunca tem default em codigo/application.yml — precisa
            // vir de fora (variavel de ambiente PRONTUARIO_LOGIN_SENHA ou
            // -Dprontuario.login.senha=... na hora de empacotar/rodar).
            throw new IllegalStateException(
                    "prontuario.login.senha nao configurada. Defina a variavel de ambiente "
                            + "PRONTUARIO_LOGIN_SENHA (ou -Dprontuario.login.senha=...) antes de iniciar o app.");
        }

        UserDetails usuaria = User.builder()
                .username(properties.getLogin().getUsuario())
                .password(encoder.encode(senha))
                .roles("MEDICA")
                .build();
        return new InMemoryUserDetailsManager(usuaria);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?saiu")
                        .permitAll());
        return http.build();
    }
}
