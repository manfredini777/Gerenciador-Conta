package GerenciadorContaBancaria;

/**
 * Esta classe de serviço é responsável por orquestrar o cálculo de tarifas.
 * Ela desacopla a lógica de cálculo da tela principal.
 */
public class TarifaService {

    /**
     * Calcula a tarifa para uma conta com base na estratégia fornecida.
     * Este método não sabe COMO a tarifa é calculada, ele apenas delega
     * a responsabilidade para a estratégia que foi passada como parâmetro.
     * * @param conta A conta para a qual a tarifa será calculada.
     * @param estrategia O enum TarifaStrategy que define a lógica do cálculo.
     * @return O valor da tarifa calculada.
     */
    public double calcularTarifa(Conta conta, TarifaStrategy estrategia) {
        // A mágica do Strategy Pattern acontece aqui:
        // Apenas chamamos o método "calcular" da estratégia escolhida.
        return estrategia.calcular(conta);
    }
}