package GerenciadorContaBancaria;


 
public class ContaCorrente extends Conta {

    public ContaCorrente(int numero, String titular, double saldo) {
        super(numero, titular, saldo);
    }
    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        
        if (this.saldo < valor) {
           
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
        }
        
        
        this.saldo -= valor;
    }
}