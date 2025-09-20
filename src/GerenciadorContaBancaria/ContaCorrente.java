package GerenciadorContaBancaria;

/**
 * A classe ContaCorrente é uma classe "concreta" que herda tudo da classe
 * abstrata "Conta". Ela representa uma conta bancária real que pode ser criada.
 */
public class ContaCorrente extends Conta {

    /**
     * Este é o construtor da ContaCorrente.
     * Ele recebe os dados e os repassa para o construtor da classe mãe (Conta).
     * @param numero O número da conta.
     * @param titular O nome do titular.
     * @param saldo O saldo inicial.
     */
    public ContaCorrente(int numero, String titular, double saldo) {
        // A palavra "super" é OBRIGATÓRIA aqui.
        // Ela serve para chamar o construtor da classe da qual herdamos (a classe Conta),
        // passando os valores para que ela possa inicializar os atributos.
        super(numero, titular, saldo);
    }

    /**
     * Esta é a implementação OBRIGATÓRIA do método abstrato "sacar".
     * A classe "Conta" define que toda conta DEVE ter um jeito de sacar,
     * e aqui nós definimos COMO uma ContaCorrente faz isso.
     * A anotação @Override é uma boa prática para indicar que estamos
     * sobrescrevendo um método da classe mãe.
     */
    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        // 1. Primeiro, verificamos se o saldo atual é suficiente para o saque.
        if (this.saldo < valor) {
            // 2. Se o saldo for insuficiente, nós "lançamos" a nossa exceção personalizada.
            // Isso vai parar a execução do método e enviar o erro para quem o chamou
            // (no nosso caso, a TelaPrincipal, que vai mostrar um JOptionPane).
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
        }
        
        // 3. Se o saldo for suficiente, a operação é realizada.
        this.saldo -= valor;
    }
}