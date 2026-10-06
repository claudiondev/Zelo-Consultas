package com.zeloconsultas.shared.util;

// Escreve numeros inteiros por extenso em portugues, no genero masculino
// (usado pra "N dias" no atestado — "dias" e masculino). Suporta 0..9999,
// faixa de sobra pra quantidade de dias de afastamento.
public final class NumeroPorExtenso {

    private static final String[] UNIDADES = {
            "zero", "um", "dois", "tres", "quatro", "cinco", "seis", "sete", "oito", "nove",
            "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete",
            "dezoito", "dezenove"
    };

    private static final String[] DEZENAS = {
            "", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"
    };

    // "cem" e caso especial (100 exato); "cento" e usado quando ha resto (101 = cento e um).
    private static final String[] CENTENAS = {
            "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos",
            "seiscentos", "setecentos", "oitocentos", "novecentos"
    };

    private NumeroPorExtenso() {
    }

    public static String escrever(int numero) {
        if (numero < 0 || numero > 9999) {
            throw new IllegalArgumentException("Suporta apenas 0..9999, recebido: " + numero);
        }
        if (numero < 20) {
            return UNIDADES[numero];
        }
        if (numero < 100) {
            return escreverDezena(numero);
        }
        if (numero < 1000) {
            return escreverCentena(numero);
        }
        return escreverMilhar(numero);
    }

    private static String escreverDezena(int numero) {
        int dezena = numero / 10;
        int unidade = numero % 10;
        if (unidade == 0) {
            return DEZENAS[dezena];
        }
        return DEZENAS[dezena] + " e " + UNIDADES[unidade];
    }

    private static String escreverCentena(int numero) {
        if (numero == 100) {
            return "cem";
        }
        int centena = numero / 100;
        int resto = numero % 100;
        if (resto == 0) {
            return CENTENAS[centena];
        }
        return CENTENAS[centena] + " e " + escrever(resto);
    }

    private static String escreverMilhar(int numero) {
        int milhar = numero / 1000;
        int resto = numero % 1000;
        String prefixo = (milhar == 1) ? "mil" : UNIDADES[milhar] + " mil";
        if (resto == 0) {
            return prefixo;
        }
        // "e" antes do resto quando ele e uma centena "redonda" ou menor que 100;
        // senao usa virgula (ex.: 1234 = "mil duzentos e trinta e quatro").
        String separador = (resto < 100 || resto % 100 == 0) ? " e " : " ";
        return prefixo + separador + escrever(resto);
    }
}
