package com.zeloconsultas.shared.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumeroPorExtensoTest {

    @ParameterizedTest
    @CsvSource({
            "0, zero",
            "1, um",
            "15, quinze",
            "20, vinte",
            "21, vinte e um",
            "30, trinta",
            "60, sessenta",
            "99, noventa e nove",
            "100, cem",
            "101, cento e um",
            "200, duzentos",
            "365, trezentos e sessenta e cinco",
            "1000, mil",
            "1234, mil duzentos e trinta e quatro"
    })
    void escreveNumeroPorExtenso(int numero, String esperado) {
        assertThat(NumeroPorExtenso.escrever(numero)).isEqualTo(esperado);
    }

    @Test
    void rejeitaNumeroForaDaFaixaSuportada() {
        assertThatThrownBy(() -> NumeroPorExtenso.escrever(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> NumeroPorExtenso.escrever(10000))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
