package GerenciadorContaBancaria;

import java.io.*;
import java.util.Scanner;

public class GerenciadorConta {
    public static void main(String[] args) {
        try {
            // Leitura do arquivo conta.txt
            BufferedReader br = new BufferedReader(new FileReader("conta.txt"));
           String linha = br.readLine();

            if (linha == null || linha.trim().isEmpty()) {
            System.out.println("Arquivo vazio ou linha vazia");
            br.close();
            return;
}

String[] dados = linha.split(",");
            int numero = Integer.parseInt(dados[0]);
            String titular = dados[1];
            double saldo = Double.parseDouble(dados[2]);

            // Criação da conta
            ContaCorrente conta = new ContaCorrente(numero, titular, saldo);
            System.out.println("Conta carregada:");
            conta.imprimirDados();

            // Solicita valor para saque
            Scanner scanner = new Scanner(System.in);
            System.out.print("Informe o valor para saque: ");
            double valorSaque = scanner.nextDouble();

            // Tenta realizar o saque
            try {
                conta.sacar(valorSaque);
                System.out.println("Saque realizado com sucesso!");
            } catch (SaldoInsuficienteException e) {
                System.out.println("Erro: " + e.getMessage());
            }

            // Grava os dados atualizados no arquivo conta_atualizada.txt
            BufferedWriter bw = new BufferedWriter(new FileWriter("conta_atualizada.txt"));
            bw.write(conta.getNumero() + "," + conta.getTitular() + "," + conta.getSaldo());
            bw.close();

            System.out.println("Dados atualizados gravados em conta_atualizada.txt");

        } catch (IOException e) {
            System.out.println("Erro de I/O: " + e.getMessage());
        }
    }
}
