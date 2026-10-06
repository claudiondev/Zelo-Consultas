package com.zeloconsultas.shared.util;

import java.io.IOException;
import java.net.ServerSocket;

// Acha uma porta livre a partir de uma preferida. Serve pro launcher nao travar
// quando a porta padrao ja esta em uso (ex.: o app foi aberto duas vezes) —
// em vez de falhar, tenta a proxima.
public final class PortDisponivel {

    private PortDisponivel() {
    }

    public static int aPartirDe(int preferida, int tentativas) {
        for (int porta = preferida; porta < preferida + tentativas; porta++) {
            if (estaLivre(porta)) {
                return porta;
            }
        }
        throw new IllegalStateException(
                "Nenhuma porta livre entre " + preferida + " e " + (preferida + tentativas - 1));
    }

    private static boolean estaLivre(int porta) {
        try (ServerSocket socket = new ServerSocket(porta)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
