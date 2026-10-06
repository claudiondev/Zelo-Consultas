package com.zeloconsultas.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

// Evita que um segundo clique no atalho (antes do navegador da primeira
// instancia abrir) tente subir outro Spring Boot inteiro, que ia falhar
// competindo pelo arquivo do banco H2 da primeira instancia. Em vez disso,
// o segundo clique so abre o navegador na instancia ja rodando e sai.
//
// Usa o bind de uma porta fixa como trava: bind de porta e atomico no
// sistema operacional (sem as inconsistencias de FileLock.tryLock() entre
// processos que existem no Windows). O socket fica aberto pelo tempo de
// vida do processo — o SO libera sozinho quando o processo termina.
public final class InstanciaUnica {

    private static final Logger log = LoggerFactory.getLogger(InstanciaUnica.class);
    private static final int PORTA_TRAVA = 58391;

    private static ServerSocket socketTrava;

    private InstanciaUnica() {
    }

    // true se esta e a primeira instancia (e ja fica dona da trava ate o
    // processo terminar). false se ja tem outra instancia rodando.
    public static boolean primeiraInstancia() {
        try {
            socketTrava = new ServerSocket(PORTA_TRAVA, 1, InetAddress.getLoopbackAddress());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
