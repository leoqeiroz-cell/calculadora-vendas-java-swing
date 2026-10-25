package br.com.senac.vendas;

import javax.swing.SwingUtilities;

/** Inicia os componentes Swing na thread de eventos. */
public final class Aplicacao {
    private Aplicacao() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JanelaVenda().setVisible(true));
    }
}
