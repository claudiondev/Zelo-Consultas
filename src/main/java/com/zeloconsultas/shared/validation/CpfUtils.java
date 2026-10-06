package com.zeloconsultas.shared.validation;

// Regras de CPF centralizadas aqui para nao duplicar o algoritmo entre o validador
// de formulario e a normalizacao usada na busca de pacientes.
public final class CpfUtils {

    private CpfUtils() {
    }

    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        return digitos.isBlank() ? null : digitos;
    }

    // Aplica a mascara 000.000.000-00 para exibicao (ex.: no atestado).
    // Se nao tiver 11 digitos, devolve o valor como veio — nao inventa formato.
    public static String formatar(String cpf) {
        String digitos = normalizar(cpf);
        if (digitos == null || digitos.length() != 11) {
            return cpf;
        }
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    // CPF e opcional no cadastro (ver modelagem do prontuario), entao string em
    // branco/nula e considerada valida aqui — quem exige presenca e outra regra.
    public static boolean isValid(String cpf) {
        String digitos = normalizar(cpf);
        if (digitos == null) {
            return true;
        }
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigitoVerificador(digitos.substring(0, 9), 10);
        int segundoDigito = calcularDigitoVerificador(digitos.substring(0, 9) + primeiroDigito, 11);

        return digitos.equals(digitos.substring(0, 9) + primeiroDigito + segundoDigito);
    }

    private static int calcularDigitoVerificador(String base, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (char c : base.toCharArray()) {
            soma += Character.getNumericValue(c) * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
