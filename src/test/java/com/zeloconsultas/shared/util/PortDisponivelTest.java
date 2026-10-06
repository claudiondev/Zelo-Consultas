package com.zeloconsultas.shared.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;

class PortDisponivelTest {

    @Test
    void devolveAPortaPreferidaQuandoEstaLivre() throws IOException {
        int preferida = portaLivreQualquer();
        assertThat(PortDisponivel.aPartirDe(preferida, 20)).isEqualTo(preferida);
    }

    @Test
    void pulaParaAProximaQuandoAPreferidaEstaOcupada() throws IOException {
        // Ocupa uma porta e verifica que a busca nao a devolve.
        try (ServerSocket ocupada = new ServerSocket(0)) {
            int portaOcupada = ocupada.getLocalPort();
            int escolhida = PortDisponivel.aPartirDe(portaOcupada, 20);
            assertThat(escolhida).isNotEqualTo(portaOcupada);
            assertThat(escolhida).isGreaterThan(portaOcupada);
        }
    }

    private int portaLivreQualquer() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
