package com.zeloconsultas.shared.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CpfUtilsFormatarTest {

    @Test
    void aplicaMascaraNos11Digitos() {
        assertThat(CpfUtils.formatar("11144477735")).isEqualTo("111.444.777-35");
    }

    @Test
    void devolveOriginalQuandoNaoTem11Digitos() {
        assertThat(CpfUtils.formatar("123")).isEqualTo("123");
    }
}
