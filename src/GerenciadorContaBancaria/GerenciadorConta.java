
package GerenciadorContaBancaria;

import view.TelaPrincipal; 
import javax.swing.SwingUtilities;

public class GerenciadorConta {

   
    public static void main(String[] args) {
        
       
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                      TelaPrincipal tela = new TelaPrincipal();
                      tela.setVisible(true);
            }
        });
    }
}