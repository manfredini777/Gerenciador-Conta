package GerenciadorContaBancaria;

import dao.ContaDAO;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContaService {

    private List<ContaCorrente> contas; // Mantém uma cópia em memória para a GUI
    private ContaDAO contaDAO;

    public ContaService() {
        this.contaDAO = new ContaDAO();
        // Na inicialização, carrega tudo do banco de dados
        this.contas = this.contaDAO.listar(); 
    }

    public List<ContaCorrente> getContas() {
        return this.contas;
    }
    
    public void adicionarConta(ContaCorrente conta) {
        this.contaDAO.inserir(conta); // 1. Insere no banco
        this.contas.add(conta);       // 2. Adiciona na lista em memória
    }

    public void depositar(ContaCorrente conta, double valor) {
        conta.depositar(valor);
        this.contaDAO.atualizarSaldo(conta.getNumero(), conta.getSaldo()); // Atualiza no banco
    }

    public void sacar(ContaCorrente conta, double valor) throws SaldoInsuficienteException {
        conta.sacar(valor);
        this.contaDAO.atualizarSaldo(conta.getNumero(), conta.getSaldo()); // Atualiza no banco
    }
    
    // Filtros e ordenações continuam funcionando na lista em memória
    public List<ContaCorrente> filtrarContas(Predicate<ContaCorrente> criterio) {
        return this.contas.stream().filter(criterio).collect(Collectors.toList());
    }

    public void ordenarContas(Comparator<ContaCorrente> criterio) {
        this.contas.sort(criterio);
    }
}