package com.zeloconsultas.shared.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CpfUtilsTest {

    @Test
    void aceitaCpfComDigitoVerificadorCorreto() {
        assertThat(CpfUtils.isValid("111.444.777-35")).isTrue();
    }

    @Test
    void rejeitaCpfComDigitoVerificadorErrado() {
        assertThat(CpfUtils.isValid("111.444.777-36")).isFalse();
    }

    @Test
    void rejeitaCpfComTodosOsDigitosIguais() {
        assertThat(CpfUtils.isValid("111.111.111-11")).isFalse();
    }

    @Test
    void rejeitaCpfComQuantidadeErradaDeDigitos() {
        assertThat(CpfUtils.isValid("123456")).isFalse();
    }

    // CPF e opcional no cadastro (ver plano) — nulo/vazio nao pode reprovar a validacao.
    @Test
    void aceitaCpfNuloOuEmBrancoPorSerOpcional() {
        assertThat(CpfUtils.isValid(null)).isTrue();
        assertThat(CpfUtils.isValid("")).isTrue();
        assertThat(CpfUtils.isValid("   ")).isTrue();
    }

    @Test
    void normalizaRemovendoMascara() {
        assertThat(CpfUtils.normalizar("111.444.777-35")).isEqualTo("11144477735");
    }

    @Test
    void normalizaCpfVazioParaNulo() {
        assertThat(CpfUtils.normalizar("")).isNull();
        assertThat(CpfUtils.normalizar(null)).isNull();
    }
}
