package view;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.io.FileWriter;
import java.io.IOException;
import GerenciadorContaBancaria.ContaCorrente;
import GerenciadorContaBancaria.ContaService;
import GerenciadorContaBancaria.Movimentacao;
import GerenciadorContaBancaria.SaldoInsuficienteException;
import GerenciadorContaBancaria.TarifaService;
import GerenciadorContaBancaria.TarifaStrategy;

public class TelaPrincipal extends JFrame {

   
    private JTable tabelaContas;
    private DefaultTableModel modeloTabela;
    private JLabel labelInfoConta;
    private JButton botaoSacar, botaoDepositar, botaoSalvar;
    private JButton botaoFiltroSaldo, botaoFiltroPares, botaoLimparFiltro;
    private JButton botaoOrdenarSaldo, botaoOrdenarTitular;
    private JButton botaoCalcularTarifa;

   
    private JButton botaoGerarExtrato;
    private JTextArea areaExtrato;
    private JScrollPane scrollParaExtrato;

    
    private ContaService contaService;
    private TarifaService tarifaService;

    public TelaPrincipal() {
        setTitle("Gerenciador Avançado de Contas Bancárias");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contaService = new ContaService();
        tarifaService = new TarifaService();

        inicializarComponentes();
        configurarAcoes();
        atualizarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        
        String[] colunas = {"Número", "Titular", "Saldo (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaContas = new JTable(modeloTabela);
        add(new JScrollPane(tabelaContas), BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout(0, 10));
        labelInfoConta = new JLabel("Bem-vindo! Selecione uma conta ou ação.", SwingConstants.CENTER);
        painelSul.add(labelInfoConta, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(3, 3, 5, 5));
        
        
        botaoSacar = new JButton("Sacar");
        botaoDepositar = new JButton("Depositar");
        botaoFiltroSaldo = new JButton("Filtrar Saldo > 5000");
        botaoFiltroPares = new JButton("Filtrar Contas Pares");
        botaoLimparFiltro = new JButton("Limpar Filtro");
        botaoOrdenarSaldo = new JButton("Ordenar por Saldo");
        botaoOrdenarTitular = new JButton("Ordenar por Titular");
        botaoCalcularTarifa = new JButton("Calcular Tarifa");
        botaoGerarExtrato = new JButton("Gerar Extrato"); // 

        
        painelBotoes.add(botaoSacar);
        painelBotoes.add(botaoDepositar);
        painelBotoes.add(botaoFiltroSaldo);
        painelBotoes.add(botaoFiltroPares);
        painelBotoes.add(botaoLimparFiltro);
        painelBotoes.add(botaoOrdenarSaldo);
        painelBotoes.add(botaoOrdenarTitular);
        painelBotoes.add(botaoCalcularTarifa);
        painelBotoes.add(botaoGerarExtrato); 

        painelSul.add(painelBotoes, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);
        
     
        areaExtrato = new JTextArea("O extrato da conta selecionada aqui.");
        areaExtrato.setEditable(false);
        areaExtrato.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaExtrato.setMargin(new Insets(5, 5, 5, 5));
        scrollParaExtrato = new JScrollPane(areaExtrato);
        scrollParaExtrato.setPreferredSize(new Dimension(350, 0));
        add(scrollParaExtrato, BorderLayout.EAST); 
    }

    private void configurarAcoes() {
        botaoDepositar.addActionListener(e -> realizarOperacao("Depositar"));
        botaoSacar.addActionListener(e -> realizarOperacao("Sacar"));

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

        botaoOrdenarSaldo.addActionListener(e -> {
            Comparator<ContaCorrente> porSaldoDesc = Comparator.comparing(ContaCorrente::getSaldo).reversed();
            contaService.ordenarContas(porSaldoDesc);
            atualizarTabela();
            labelInfoConta.setText("Contas ordenadas por saldo (maior para menor).");
        });

        botaoOrdenarTitular.addActionListener(e -> {
            Comparator<ContaCorrente> porTitular = Comparator.comparing(ContaCorrente::getTitular, String.CASE_INSENSITIVE_ORDER);
            contaService.ordenarContas(porTitular);
            atualizarTabela();
            labelInfoConta.setText("Contas ordenadas por titular (A-Z).");
        });
        
        botaoCalcularTarifa.addActionListener(e -> {
            int linhaSelecionada = tabelaContas.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ContaCorrente contaSelecionada = contaService.getContas().get(tabelaContas.convertRowIndexToModel(linhaSelecionada));
            
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
        
      
        botaoGerarExtrato.addActionListener(e -> btnGerarExtratoActionPerformed());

        tabelaContas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaContas.getSelectedRow() != -1) {
                int linhaView = tabelaContas.getSelectedRow();
                int linhaModel = tabelaContas.convertRowIndexToModel(linhaView);
                String titular = (String) modeloTabela.getValueAt(linhaModel, 1);
                labelInfoConta.setText("Conta selecionada: " + titular);
            }
        });
    }

    public void atualizarTabela(List<ContaCorrente> contasParaExibir) {
        modeloTabela.setRowCount(0);
        for (ContaCorrente conta : contasParaExibir) {
            modeloTabela.addRow(new Object[]{
                conta.getNumero(),
                conta.getTitular(),
                String.format("%.2f", conta.getSaldo())
            });
        }
    }
    
    public void atualizarTabela() {
        atualizarTabela(contaService.getContas());
    }
    
    private void realizarOperacao(String tipoOperacao) {
        int linhaView = tabelaContas.getSelectedRow();
        if (linhaView == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta na tabela primeiro.");
            return;
        }
        int linhaModel = tabelaContas.convertRowIndexToModel(linhaView);
        ContaCorrente contaSelecionada = contaService.getContas().get(linhaModel);

        String valorStr = JOptionPane.showInputDialog(this, "Digite o valor para " + tipoOperacao + ":");

        if (valorStr != null && !valorStr.trim().isEmpty()) {
            try {
                double valor = Double.parseDouble(valorStr.replace(",", "."));
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
                tabelaContas.setRowSelectionInterval(linhaView, linhaView); // Mantém a seleção

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Por favor, digite apenas números.");
            } catch (SaldoInsuficienteException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void btnGerarExtratoActionPerformed() {
        int selectedRow = tabelaContas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta na tabela primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numeroConta = (int) tabelaContas.getValueAt(selectedRow, 0);
        String titular = (String) tabelaContas.getValueAt(selectedRow, 1);
        
        List<Movimentacao> extrato = this.contaService.gerarExtrato(numeroConta);

        StringBuilder sb = new StringBuilder();
        sb.append("--- Extrato da Conta: ").append(numeroConta).append(" - ").append(titular).append(" ---\n");
        sb.append("---\n\n");
        
        if (extrato.isEmpty()) {
            sb.append("Nenhuma movimentação encontrada para esta conta.\n");
        } else {
            for (Movimentacao mov : extrato) {
                sb.append("Data: ").append(mov.getData().toLocalDate());
                sb.append("  Tipo: ").append(String.format("%-10s", mov.getTipo()));
                sb.append("  Valor: R$ ").append(String.format("%.2f", mov.getValor()));
                sb.append("\n");
            }
        }
        
        areaExtrato.setText(sb.toString());
        salvarExtratoEmArquivo(numeroConta, sb.toString());
    }

    private void salvarExtratoEmArquivo(int numeroConta, String conteudo) {
        String nomeArquivo = "extrato_" + numeroConta + ".txt";
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            writer.write(conteudo);
            JOptionPane.showMessageDialog(this, 
                    "Extrato também foi salvo com sucesso no arquivo:\n" + nomeArquivo, 
                    "Exportação Concluída", 
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                    "Ocorreu um erro ao salvar o arquivo de extrato.", 
                    "Erro de Arquivo", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}