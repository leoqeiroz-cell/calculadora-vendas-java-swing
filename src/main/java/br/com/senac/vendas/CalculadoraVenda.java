package br.com.senac.vendas;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Regras de entrada e cálculo, separadas da apresentação Swing. */
public final class CalculadoraVenda {
    private static final BigDecimal LIMITE = new BigDecimal("500.00");
    private static final BigDecimal CEM = new BigDecimal("100");

    private CalculadoraVenda() { }

    private static BigDecimal lerNumero(String texto, String nome) {
        // Sem separadores de milhar: evita interpretar 1.000 como mil ou um.
        if (texto == null || !texto.trim().matches("[0-9]+([,.][0-9]{1,2})?")) {
            throw new IllegalArgumentException(nome
                    + ": informe um número sem separador de milhar, com até duas casas decimais.");
        }
        return new BigDecimal(texto.trim().replace(',', '.'));
    }

    public static BigDecimal lerVenda(String texto) {
        BigDecimal venda = lerNumero(texto, "Valor da venda");
        if (venda.signum() <= 0) {
            throw new IllegalArgumentException("O valor da venda deve ser maior que zero.");
        }
        return venda.setScale(2);
    }

    public static BigDecimal lerPercentual(String texto) {
        BigDecimal percentual = lerNumero(texto, "Desconto");
        validarPercentual(percentual);
        return percentual;
    }

    public static boolean permiteDesconto(BigDecimal venda) {
        return venda.compareTo(LIMITE) > 0;
    }

    private static void validarPercentual(BigDecimal percentual) {
        if (percentual == null || percentual.signum() < 0 || percentual.compareTo(CEM) > 0) {
            throw new IllegalArgumentException("O desconto deve estar entre 0 e 100 por cento.");
        }
    }

    public static BigDecimal calcularTotal(BigDecimal venda, BigDecimal percentual) {
        if (venda == null || venda.signum() <= 0) {
            throw new IllegalArgumentException("O valor da venda deve ser maior que zero.");
        }
        validarPercentual(percentual);
        BigDecimal aplicado = permiteDesconto(venda) ? percentual : BigDecimal.ZERO;
        // BigDecimal evita erros de representação binária nos valores monetários.
        return venda.multiply(BigDecimal.ONE.subtract(aplicado.divide(CEM)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
