package view;

// Importações que o Java Swing precisa para funcionar
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Importações das suas outras classes do projeto
import GerenciadorContaBancaria.ContaCorrente;
import GerenciadorContaBancaria.ContaService;
import GerenciadorContaBancaria.SaldoInsuficienteException;

// A classe vira uma janela usando "extends JFrame"
public class TelaPrincipal extends JFrame {

    // --- Declaração dos componentes visuais que vamos usar ---
    private JTable tabelaContas;
    private DefaultTableModel modeloTabela;
    private JButton botaoSacar, botaoDepositar, botaoSalvar;

    // --- Objeto que contém a lógica do nosso programa ---
    private ContaService contaService;

    // Construtor: é o código que executa assim que a janela é criada
    public TelaPrincipal() {
        
        // --- 1. Configurações básicas da janela ---
        setTitle("Gerenciador de Contas");
        setSize(800, 600); // Largura e altura da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Faz o programa fechar ao clicar no "X"
        // A janela vai aparecer no canto superior esquerdo da tela.

        // --- 2. Cria e prepara os componentes visuais ---
        
        // Define o layout principal da janela
        setLayout(new BorderLayout());

        // Cria a tabela
        String[] colunas = {"Número", "Titular", "Saldo (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaContas = new JTable(modeloTabela);
        
        // Adiciona a tabela com uma barra de rolagem no centro da janela
        add(new JScrollPane(tabelaContas), BorderLayout.CENTER);

        // Cria um painel na parte de baixo para colocar os botões
        JPanel painelBotoes = new JPanel();
        botaoSacar = new JButton("Sacar");
        botaoDepositar = new JButton("Depositar");
        botaoSalvar = new JButton("Salvar Alterações");
        
        // Adiciona os botões ao painel
        painelBotoes.add(botaoSacar);
        painelBotoes.add(botaoDepositar);
        painelBotoes.add(botaoSalvar);
        
        // Adiciona o painel com os botões na parte de baixo da janela
        add(painelBotoes, BorderLayout.SOUTH);

        // --- 3. Define o que cada botão faz ---
        
        // Ação do botão Depositar
        botaoDepositar.addActionListener(e -> realizarOperacao("Depositar"));

        // Ação do botão Sacar
        botaoSacar.addActionListener(e -> realizarOperacao("Sacar"));

        // Ação do botão Salvar
        botaoSalvar.addActionListener(e -> {
            try {
                contaService.salvarContas("contas_atualizadas.txt");
                JOptionPane.showMessageDialog(this, "Contas salvas com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar contas.");
            }
        });

        // --- 4. Carrega os dados ---
        
        // Cria o nosso serviço de contas
        contaService = new ContaService();
        try {
            // Pede para o serviço carregar os dados do arquivo
            contaService.carregarContas("contas.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o arquivo de contas.");
        }
        
        // Manda a tabela se atualizar com os dados que foram carregados
        atualizarTabela();
    }

    // Método que limpa e preenche a tabela com os dados mais recentes
    public void atualizarTabela() {
        modeloTabela.setRowCount(0); // Limpa todas as linhas antigas
        List<ContaCorrente> contas = contaService.getContas();
        for (ContaCorrente conta : contas) {
            // Adiciona uma nova linha na tabela para cada conta
            modeloTabela.addRow(new Object[]{
                conta.getNumero(),
                conta.getTitular(),
                conta.getSaldo()
            });
        }
    }

    // Método que é chamado quando clicamos em "Sacar" ou "Depositar"
    private void realizarOperacao(String tipoOperacao) {
        // Pega o número da linha que o usuário selecionou na tabela
        int linhaSelecionada = tabelaContas.getSelectedRow();

        // Se for -1, significa que nenhuma linha foi selecionada
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta na tabela primeiro.");
            return; // Para a execução do método aqui
        }

        // Pega o objeto da conta que corresponde à linha selecionada
        ContaCorrente contaSelecionada = contaService.getContas().get(linhaSelecionada);
        
        // Pede ao usuário para digitar o valor
        String valorStr = JOptionPane.showInputDialog(this, "Digite o valor para " + tipoOperacao + ":");

        // Verifica se o usuário digitou algo e não clicou em "Cancelar"
        if (valorStr != null && !valorStr.isEmpty()) {
            try {
                // Tenta converter o texto digitado para um número
                double valor = Double.parseDouble(valorStr);

                if (tipoOperacao.equals("Depositar")) {
                    contaService.depositar(contaSelecionada, valor);
                } else if (tipoOperacao.equals("Sacar")) {
                    contaService.sacar(contaSelecionada, valor);
                }
                
                JOptionPane.showMessageDialog(this, tipoOperacao + " realizado com sucesso!");
                atualizarTabela(); // Atualiza a tabela para mostrar o novo saldo

            } catch (NumberFormatException ex) {
                // Este erro acontece se o usuário digitar letras em vez de números
                JOptionPane.showMessageDialog(this, "Valor inválido. Por favor, digite apenas números.");
            } catch (SaldoInsuficienteException ex) {
                // Este erro acontece se o saque falhar por falta de saldo
                JOptionPane.showMessageDialog(this, "Erro: Saldo insuficiente.");
            }
        }
    }
}