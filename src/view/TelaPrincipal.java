package view;

// Importações do Java Swing para a interface gráfica
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

// Importações das suas outras classes do projeto
import GerenciadorContaBancaria.ContaCorrente;
import GerenciadorContaBancaria.ContaService;
import GerenciadorContaBancaria.SaldoInsuficienteException;
import GerenciadorContaBancaria.TarifaService; // <-- NOVO
import GerenciadorContaBancaria.TarifaStrategy;  // <-- NOVO

public class TelaPrincipal extends JFrame {

    // --- Declaração dos componentes visuais ---
    private JTable tabelaContas;
    private DefaultTableModel modeloTabela;
    private JLabel labelInfoConta; // Para dar feedback ao usuário
    
    // Botões de operação
    private JButton botaoSacar, botaoDepositar, botaoSalvar;
    
    // Botões de filtro (Predicate)
    private JButton botaoFiltroSaldo, botaoFiltroPares, botaoLimparFiltro;
    
    // Botões de ordenação (Comparator)
    private JButton botaoOrdenarSaldo, botaoOrdenarTitular;
    
    // Botão de estratégia (Enum)
    private JButton botaoCalcularTarifa;

    // --- Objetos que contêm a lógica do nosso programa ---
    private ContaService contaService;
    private TarifaService tarifaService; // <-- NOVO

    // Construtor: código que roda quando a janela é criada
    public TelaPrincipal() {
        // --- 1. Configurações básicas da janela ---
        setTitle("Gerenciador Avançado de Contas Bancárias");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // --- 2. Inicializa os serviços de lógica ---
        contaService = new ContaService();
        tarifaService = new TarifaService(); // Instancia o novo serviço
        
        
        // --- 3. Monta a interface gráfica ---
        inicializarComponentes(); // Método que cria e organiza os componentes visuais
        configurarAcoes();      // Método que define o que cada botão faz
        
        // --- 4. Popula a tabela com os dados iniciais ---
        atualizarTabela();
    }

    // Método para criar e organizar os componentes visuais na tela
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10)); // Layout principal com espaçamento

        // Tabela no centro
        String[] colunas = {"Número", "Titular", "Saldo (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Impede edição direta na tabela
        };
        tabelaContas = new JTable(modeloTabela);
        add(new JScrollPane(tabelaContas), BorderLayout.CENTER);

        // Painel na parte de baixo para informações e botões
        JPanel painelSul = new JPanel(new BorderLayout(0, 10));
        labelInfoConta = new JLabel("Bem-vindo! Selecione uma conta ou ação.", SwingConstants.CENTER);
        painelSul.add(labelInfoConta, BorderLayout.NORTH);

        // Painel de botões com layout em grade para melhor organização
        JPanel painelBotoes = new JPanel(new GridLayout(3, 3, 5, 5));
        
        // Instanciando todos os botões
        botaoSacar = new JButton("Sacar");
        botaoDepositar = new JButton("Depositar");
        botaoSalvar = new JButton("Salvar Alterações");
        botaoFiltroSaldo = new JButton("Filtrar Saldo > 5000");
        botaoFiltroPares = new JButton("Filtrar Contas Pares");
        botaoLimparFiltro = new JButton("Limpar Filtro");
        botaoOrdenarSaldo = new JButton("Ordenar por Saldo");
        botaoOrdenarTitular = new JButton("Ordenar por Titular");
        botaoCalcularTarifa = new JButton("Calcular Tarifa");

        // Adicionando os botões ao painel
        painelBotoes.add(botaoSacar);
        painelBotoes.add(botaoFiltroSaldo);
        painelBotoes.add(botaoFiltroPares);
        painelBotoes.add(botaoLimparFiltro);
        painelBotoes.add(botaoOrdenarSaldo);
        painelBotoes.add(botaoOrdenarTitular);
        painelBotoes.add(botaoCalcularTarifa);
        
        painelSul.add(painelBotoes, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);
    }
    
    // Método que define o que cada botão e componente interativo faz
    private void configurarAcoes() {
        // Ações de operação básica
        botaoDepositar.addActionListener(e -> realizarOperacao("Depositar"));
        botaoSacar.addActionListener(e -> realizarOperacao("Sacar"));
       // botaoSalvar.addActionListener(e -> {
            //try {
               // contaService.salvarContas("contas_atualizadas.txt");
             //   JOptionPane.showMessageDialog(this, "Contas salvas com sucesso!");
           // } catch (Exception ex) {
             //   JOptionPane.showMessageDialog(this, "Erro ao salvar contas.");
           // }
       // });

        // Ações de filtro (Predicate)
        botaoFiltroSaldo.addActionListener(e -> {
            Predicate<ContaCorrente> saldoMaiorQue5000 = conta -> conta.getSaldo() > 5000;
            List<ContaCorrente> contasFiltradas = contaService.filtrarContas(saldoMaiorQue5000);
            atualizarTabela(contasFiltradas);
            labelInfoConta.setText(contasFiltradas.size() + " conta(s) encontrada(s) com saldo > R$ 5.000.");
        });

        botaoFiltroPares.addActionListener(e -> {
            Predicate<ContaCorrente> numeroPar = conta -> conta.getNumero() % 2 == 0;
            List<ContaCorrente> contasFiltradas = contaService.filtrarContas(numeroPar);
            atualizarTabela(contasFiltradas);
            labelInfoConta.setText(contasFiltradas.size() + " conta(s) encontrada(s) com número par.");
        });
        
        botaoLimparFiltro.addActionListener(e -> {
            atualizarTabela();
            labelInfoConta.setText("Filtro removido. Exibindo todas as contas.");
        });

        // Ações de ordenação (Comparator)
        botaoOrdenarSaldo.addActionListener(e -> {
            Comparator<ContaCorrente> porSaldoDesc = (c1, c2) -> Double.compare(c2.getSaldo(), c1.getSaldo());
            contaService.ordenarContas(porSaldoDesc);
            atualizarTabela();
            labelInfoConta.setText("Contas ordenadas por saldo (maior para menor).");
        });

        botaoOrdenarTitular.addActionListener(e -> {
            Comparator<ContaCorrente> porTitular = (c1, c2) -> c1.getTitular().compareToIgnoreCase(c2.getTitular());
            contaService.ordenarContas(porTitular);
            atualizarTabela();
            labelInfoConta.setText("Contas ordenadas por titular (A-Z).");
        });
        
        // Ação de cálculo de tarifa (Enum Strategy)
        botaoCalcularTarifa.addActionListener(e -> {
            int linhaSelecionada = tabelaContas.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ContaCorrente contaSelecionada = contaService.getContas().get(linhaSelecionada);
            
            TarifaStrategy[] estrategias = TarifaStrategy.values();
            TarifaStrategy estrategiaEscolhida = (TarifaStrategy) JOptionPane.showInputDialog(
                this, "Escolha a estratégia de tarifa:", "Cálculo de Tarifa", 
                JOptionPane.QUESTION_MESSAGE, null, estrategias, estrategias[0]
            );
            
            if (estrategiaEscolhida != null) {
                double tarifa = tarifaService.calcularTarifa(contaSelecionada, estrategiaEscolhida);
                JOptionPane.showMessageDialog(this, 
                    String.format("A tarifa para %s com a estratégia %s é: R$ %.2f", 
                        contaSelecionada.getTitular(), estrategiaEscolhida.name(), tarifa),
                    "Resultado da Tarifa", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Ação para quando uma linha da tabela é selecionada
        tabelaContas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaContas.getSelectedRow() != -1) {
                int linha = tabelaContas.getSelectedRow();
                String titular = (String) modeloTabela.getValueAt(linha, 1);
                labelInfoConta.setText("Conta selecionada: " + titular);
            }
        });
    }

    // Método que preenche a tabela com uma lista específica (para filtros)
    public void atualizarTabela(List<ContaCorrente> contasParaExibir) {
        modeloTabela.setRowCount(0);
        for (ContaCorrente conta : contasParaExibir) {
            modeloTabela.addRow(new Object[]{
                conta.getNumero(),
                conta.getTitular(),
                String.format("%.2f", conta.getSaldo()) // Formata o saldo para 2 casas decimais
            });
        }
    }
    
    // Sobrecarga do método para atualizar com a lista completa do serviço
    public void atualizarTabela() {
        atualizarTabela(contaService.getContas());
    }
    
    // Método para saque e depósito
    private void realizarOperacao(String tipoOperacao) {
        int linhaSelecionada = tabelaContas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta na tabela primeiro.");
            return;
        }

        ContaCorrente contaSelecionada = contaService.getContas().get(linhaSelecionada);
        String valorStr = JOptionPane.showInputDialog(this, "Digite o valor para " + tipoOperacao + ":");

        if (valorStr != null && !valorStr.trim().isEmpty()) {
            try {
                double valor = Double.parseDouble(valorStr.replace(",", ".")); // Aceita vírgula e ponto
                if (valor <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor deve ser positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tipoOperacao.equals("Depositar")) {
                    contaService.depositar(contaSelecionada, valor);
                } else if (tipoOperacao.equals("Sacar")) {
                    contaService.sacar(contaSelecionada, valor);
                }
                
                JOptionPane.showMessageDialog(this, tipoOperacao + " realizado com sucesso!");
                atualizarTabela();
                tabelaContas.setRowSelectionInterval(linhaSelecionada, linhaSelecionada); // Mantém a seleção

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Por favor, digite apenas números.");
            } catch (SaldoInsuficienteException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
}