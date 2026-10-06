package com.zeloconsultas;

import com.zeloconsultas.shared.util.InstanciaUnica;
import com.zeloconsultas.shared.util.PortDisponivel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZeloConsultasApplication {

    private static final boolean PERFIL_DESKTOP = "desktop".equals(System.getProperty("spring.profiles.active"));

    public static void main(String[] args) {
        // No app empacotado (perfil desktop), um segundo clique no atalho antes
        // do navegador da primeira instancia abrir tentaria subir outro Spring
        // Boot inteiro, que falharia competindo pelo arquivo do banco H2 da
        // primeira instancia. Em vez disso, so abre o navegador na instancia
        // ja rodando (sempre na porta fixa 8080 nesse perfil) e sai, sem tocar
        // no banco.
        if (PERFIL_DESKTOP && !InstanciaUnica.primeiraInstancia()) {
            abrirNavegador("http://localhost:8080");
            return;
        }

        // Se ninguem fixou a porta, escolhe uma livre a partir de 8080 — assim o
        // app nao trava se a porta padrao ja estiver ocupada por outra coisa. No
        // perfil desktop a trava de instancia unica acima ja garante que 8080
        // esta livre pra nos, entao so cai no proximo se algo externo a estiver
        // usando. Um --server.port explicito na linha de comando tem prioridade.
        if (System.getProperty("server.port") == null) {
            System.setProperty("server.port", String.valueOf(PortDisponivel.aPartirDe(8080, 20)));
        }

        SpringApplication.run(ZeloConsultasApplication.class, args);
    }

    private static void abrirNavegador(String url) {
        try {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
        } catch (Exception ignored) {
            // Melhor esforco: se nem isso funcionar, a usuaria ainda pode abrir
            // o navegador manualmente, a instancia ja rodando continua de pe.
        }
    }
}
