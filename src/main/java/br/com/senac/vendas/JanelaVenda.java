package br.com.senac.vendas;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Interface construída inteiramente por código, sem editor visual. */
public final class JanelaVenda extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JTextField campoVenda = new JTextField(20);
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public JanelaVenda() {
        super("Calculadora de vendas - Atividade 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel conteudo = new JPanel(new BorderLayout(12, 16));
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel entrada = new JPanel(new GridLayout(0, 1, 0, 8));
        JLabel rotulo = new JLabel("Valor da venda (R$):");
        rotulo.setLabelFor(campoVenda);
        rotulo.setDisplayedMnemonic(KeyEvent.VK_V);
        campoVenda.setToolTipText("Exemplo: 600,00. Não use separador de milhar.");
        campoVenda.getAccessibleContext().setAccessibleDescription(
                "Valor positivo em reais, com até duas casas decimais, sem separador de milhar.");
        entrada.add(rotulo);
        entrada.add(campoVenda);
        entrada.add(new JLabel("Exemplo: 600,00 — sem separador de milhar."));
        entrada.add(new JLabel("Desconto disponível para vendas acima de R$ 500,00."));

        JButton botao = new JButton("Calcular total");
        botao.setMnemonic(KeyEvent.VK_C);
        botao.addActionListener(evento -> processarVenda());
        conteudo.add(entrada, BorderLayout.CENTER);
        conteudo.add(botao, BorderLayout.SOUTH);
        setContentPane(conteudo);
        getRootPane().setDefaultButton(botao);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private void processarVenda() {
        final BigDecimal venda;
        try {
            venda = CalculadoraVenda.lerVenda(campoVenda.getText());
        } catch (IllegalArgumentException erro) {
            mostrarErro(erro.getMessage());
            campoVenda.requestFocusInWindow();
            campoVenda.selectAll();
            return;
        }

        BigDecimal percentual = BigDecimal.ZERO;
        if (CalculadoraVenda.permiteDesconto(venda)) {
            percentual = solicitarDesconto();
            if (percentual == null) {
                campoVenda.requestFocusInWindow();
                return; // Cancelar interrompe esta operação e preserva a venda.
            }
        }
        BigDecimal total = CalculadoraVenda.calcularTotal(venda, percentual);
        JOptionPane.showMessageDialog(this,
                "Valor da venda: " + moeda.format(venda)
                + "\nDesconto: " + percentual.stripTrailingZeros().toPlainString().replace('.', ',') + "%"
                + "\nValor descontado: " + moeda.format(venda.subtract(total))
                + "\nTotal a pagar: " + moeda.format(total),
                "Resultado da venda", JOptionPane.INFORMATION_MESSAGE);
        campoVenda.requestFocusInWindow();
        campoVenda.selectAll();
    }

    private BigDecimal solicitarDesconto() {
        while (true) {
            String entrada = JOptionPane.showInputDialog(this,
                    "Informe o desconto de 0 a 100 (%).\nExemplo: 10 ou 10,50. Digite apenas o número.",
                    "Percentual de desconto", JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) {
                return null;
            }
            try {
                return CalculadoraVenda.lerPercentual(entrada);
            } catch (IllegalArgumentException erro) {
                mostrarErro(erro.getMessage());
            }
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Verifique o valor informado",
                JOptionPane.WARNING_MESSAGE);
    }
}
