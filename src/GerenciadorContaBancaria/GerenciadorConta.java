// O nome do pacote continua o mesmo do seu arquivo
package GerenciadorContaBancaria;

// IMPORTANTE: Adicione estes dois imports no topo do seu arquivo
import view.TelaPrincipal; 
import javax.swing.SwingUtilities;

public class GerenciadorConta {

    /**
     * Este é o NOVO ponto de partida.
     * Sua única responsabilidade é criar e mostrar a janela da interface gráfica.
     */
    public static void main(String[] args) {
        
        // Este comando garante que a interface gráfica rode de forma segura
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Cria a sua janela
                TelaPrincipal tela = new TelaPrincipal();
                
                // 2. Torna a janela visível para o usuário
                tela.setVisible(true);
            }
        });
    }
}