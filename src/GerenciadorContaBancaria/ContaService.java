package GerenciadorContaBancaria;

import java.io.*;
import java.util.ArrayList; 
import java.util.List;

public class ContaService {
    private ArrayList<ContaCorrente> contas = new ArrayList<>();

    // Retorna todas as contas
    public List<ContaCorrente> getContas() {
        return contas;
    }

    // Adiciona nova conta na memória
    public void adicionarConta(ContaCorrente conta) {
        contas.add(conta);
    }

    // Carregar contas do arquivo
    public void carregarContas(String caminhoArquivo) throws IOException {
        contas.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                int numero = Integer.parseInt(dados[0].trim());
                String titular = dados[1].trim();
                double saldo = Double.parseDouble(dados[2].trim());
                contas.add(new ContaCorrente(numero, titular, saldo));
            }
        }
    }

    // Salvar contas em arquivo
    public void salvarContas(String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (ContaCorrente conta : contas) {
                bw.write(conta.getNumero() + "," + conta.getTitular() + "," + conta.getSaldo());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Operação de saque
    public void sacar(ContaCorrente conta, double valor) throws SaldoInsuficienteException {
        conta.sacar(valor);
        salvarContas("conta_atualizada.txt");
    }

    // Operação de depósito
    public void depositar(ContaCorrente conta, double valor) {
        conta.depositar(valor);
        salvarContas("conta_atualizada.txt");
    }
}
