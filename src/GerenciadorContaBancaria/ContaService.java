package GerenciadorContaBancaria;

import dao.ContaDAO;
import GerenciadorContaBancaria.Movimentacao; 
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContaService {

    private List<ContaCorrente> contas;
    private final ContaDAO contaDAO;

    public ContaService() {
        this.contaDAO = new ContaDAO();
        this.contas = this.contaDAO.listar(); 
    }

    public List<ContaCorrente> getContas() {
        return this.contas;
    }
    
    public void adicionarConta(ContaCorrente conta) {
        
        this.contaDAO.inserir(conta); 
        this.contas.add(conta); 
    }

    public void depositar(ContaCorrente conta, double valor) {
        if (valor <= 0) {
           
            System.err.println("Valor de depósito deve ser positivo.");
            return;
        }
        conta.depositar(valor);
       
        this.contaDAO.atualizarSaldo(conta.getNumero(), conta.getSaldo());
        
      
        Movimentacao mov = new Movimentacao(conta.getNumero(), "DEPOSITO", valor);
        this.contaDAO.inserirMovimentacao(mov);
    }

    public void sacar(ContaCorrente conta, double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
          
            System.err.println("Valor de saque deve ser positivo.");
            return;
        }
        conta.sacar(valor); 
        
        // 1. Atualiza o saldo 
        this.contaDAO.atualizarSaldo(conta.getNumero(), conta.getSaldo());
        
        
        Movimentacao mov = new Movimentacao(conta.getNumero(), "SAQUE", valor);
        this.contaDAO.inserirMovimentacao(mov);
    }
    
    public void removerConta(ContaCorrente conta) {
        this.contaDAO.remover(conta.getNumero()); 
        this.contas.remove(conta); 
    }

    
    public List<Movimentacao> gerarExtrato(int numeroConta) {
        return this.contaDAO.listarExtrato(numeroConta);
    }

   
    public List<ContaCorrente> filtrarContas(Predicate<ContaCorrente> criterio) {
        return this.contas.stream()
                          .filter(criterio)
                          .collect(Collectors.toList());
    }

    public void ordenarContas(Comparator<ContaCorrente> criterio) {
        this.contas.sort(criterio);
    }
}