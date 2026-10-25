import br.com.senac.vendas.CalculadoraVenda;
import br.com.senac.vendas.JanelaVenda;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Testes executáveis sem bibliotecas externas, incluindo os diálogos reais. */
public class VerificarAplicacao {
    private static int aprovados;
    private static JanelaVenda janela;

    public static void main(String[] args) throws Exception {
        try {
            igual("Venda com vírgula", "600.50", CalculadoraVenda.lerVenda("600,50"));
            igual("Venda com ponto", "600.50", CalculadoraVenda.lerVenda("600.50"));
            igual("Sem desconto abaixo do limite", "400.00", total("400", "10"));
            igual("Sem desconto no limite", "500.00", total("500", "10"));
            igual("Desconto de dez por cento", "540.00", total("600", "10"));
            igual("Primeiro centavo acima do limite", "450.01", total("500.01", "10"));
            igual("Desconto zero", "600.00", total("600", "0"));
            igual("Desconto integral", "0.00", total("600", "100"));
            igual("Percentual decimal e arredondamento", "537.01", total("600.01", "10.50"));
            for (String entrada : new String[]{"", "abc", "-1", "0", "1.000,00", "12,345", "NaN"}) {
                invalido("Venda inválida: [" + entrada + "]", () -> CalculadoraVenda.lerVenda(entrada));
            }
            for (String entrada : new String[]{"", "abc", "-1", "100.01", "10%"}) {
                invalido("Desconto inválido: [" + entrada + "]", () -> CalculadoraVenda.lerPercentual(entrada));
            }
            SwingUtilities.invokeAndWait(() -> { janela = new JanelaVenda(); janela.setVisible(true); });
            capturar(janela, "01_janela_principal.png");
            iniciar("400");
            conferirResultado("400,00", "02_venda_sem_desconto.png");
            iniciar("500");
            conferirResultado("500,00", "03_venda_no_limite.png");
            iniciar("600");
            JDialog desconto = aguardar("Percentual de desconto");
            capturar(desconto, "04_dialogo_desconto.png");
            informar(desconto, "10");
            conferirResultado("540,00", "05_resultado_com_desconto.png");
            iniciar("abc");
            JDialog erro = aguardar("Verifique o valor informado");
            capturar(erro, "06_validacao_venda.png");
            clicar(erro, "OK");
            passou("Interface rejeita venda não numérica");
            iniciar("600");
            informar(aguardar("Percentual de desconto"), "101");
            erro = aguardar("Verifique o valor informado");
            capturar(erro, "07_validacao_desconto.png");
            clicar(erro, "OK");
            desconto = aguardar("Percentual de desconto");
            clicar(desconto, UIManager.getString("OptionPane.cancelButtonText"));
            Thread.sleep(200);
            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog && w.isShowing()) throw new AssertionError("Diálogo permaneceu após cancelar");
            }
            passou("Desconto inválido permite corrigir; cancelar encerra sem resultado");
            System.out.println("TOTAL: " + aprovados + " verificações aprovadas.");
        } finally {
            SwingUtilities.invokeAndWait(() -> { for (Window w : Window.getWindows()) w.dispose(); });
        }
    }

    private static BigDecimal total(String venda, String percentual) {
        return CalculadoraVenda.calcularTotal(CalculadoraVenda.lerVenda(venda), CalculadoraVenda.lerPercentual(percentual));
    }
    private static void igual(String nome, String esperado, BigDecimal obtido) {
        if (obtido.compareTo(new BigDecimal(esperado)) != 0) throw new AssertionError(nome + ": " + obtido);
        passou(nome + " => " + obtido);
    }
    private static void invalido(String nome, Runnable acao) {
        try { acao.run(); } catch (IllegalArgumentException esperado) { passou(nome); return; }
        throw new AssertionError(nome);
    }
    private static void passou(String nome) { aprovados++; System.out.println("APROVADO: " + nome); }
    private static List<Component> componentes(Container raiz) {
        List<Component> lista = new ArrayList<>();
        for (Component c : raiz.getComponents()) { lista.add(c); if (c instanceof Container) lista.addAll(componentes((Container)c)); }
        return lista;
    }
    private static void iniciar(String valor) throws Exception {
        SwingUtilities.invokeAndWait(() -> componentes(janela).stream().filter(c -> c instanceof JTextField)
                .map(c -> (JTextField)c).findFirst().orElseThrow().setText(valor));
        SwingUtilities.invokeLater(() -> componentes(janela).stream().filter(c -> c instanceof JButton)
                .map(c -> (JButton)c).findFirst().orElseThrow().doClick());
    }
    private static JDialog aguardar(String titulo) throws Exception {
        for (int i = 0; i < 100; i++) {
            JDialog[] encontrado = new JDialog[1];
            SwingUtilities.invokeAndWait(() -> {
                for (Window w : Window.getWindows()) if (w instanceof JDialog && w.isShowing()
                        && ((JDialog)w).getTitle().equals(titulo)) encontrado[0] = (JDialog)w;
            });
            if (encontrado[0] != null) return encontrado[0];
            Thread.sleep(50);
        }
        throw new AssertionError("Diálogo não apareceu: " + titulo);
    }
    private static void clicar(JDialog dialogo, String texto) throws Exception {
        SwingUtilities.invokeAndWait(() -> componentes(dialogo).stream().filter(c -> c instanceof JButton)
                .map(c -> (JButton)c).filter(b -> b.getText().equals(texto)).findFirst().orElseThrow().doClick());
    }
    private static void informar(JDialog dialogo, String valor) throws Exception {
        SwingUtilities.invokeAndWait(() -> componentes(dialogo).stream().filter(c -> c instanceof JTextField)
                .map(c -> (JTextField)c).findFirst().orElseThrow().setText(valor));
        clicar(dialogo, "OK");
    }
    private static void conferirResultado(String esperado, String imagem) throws Exception {
        JDialog resultado = aguardar("Resultado da venda");
        SwingUtilities.invokeAndWait(() -> {
            boolean correto = componentes(resultado).stream().filter(c -> c instanceof JLabel)
                    .map(c -> ((JLabel)c).getText()).anyMatch(t -> t != null && t.contains("Total a pagar:") && t.contains(esperado));
            if (!correto) throw new AssertionError("Resultado inesperado: " + esperado);
        });
        capturar(resultado, imagem);
        clicar(resultado, "OK");
        passou("Interface apresenta total " + esperado);
    }
    private static void capturar(Window janela, String nome) throws Exception {
        // Renderiza os componentes reais da janela exibida durante o teste.
        // Não é uma captura da área de trabalho nem uma ilustração simulada.
        SwingUtilities.invokeAndWait(() -> {
            BufferedImage imagem = new BufferedImage(janela.getWidth(), janela.getHeight(), BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = imagem.createGraphics();
            janela.paint(g); g.dispose();
            try {
                File pasta = new File("evidencias"); pasta.mkdirs();
                ImageIO.write(imagem, "png", new File(pasta, nome));
            } catch (java.io.IOException erro) { throw new RuntimeException(erro); }
        });
    }
}
