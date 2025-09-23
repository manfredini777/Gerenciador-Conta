package GerenciadorContaBancaria;

public enum TarifaStrategy {
    
    FIXA(10.0) {
        @Override
        public double calcular(Conta conta) {
            return this.getValorBase();
        }
    },
    PERCENTUAL(0.01) { // 1%
        @Override
        public double calcular(Conta conta) {
            return conta.getSaldo() * this.getValorBase();
        }
    },
    ISENTA(0.0) {
        @Override
        public double calcular(Conta conta) {
            return this.getValorBase();
        }
    };

    private final double valorBase;

    TarifaStrategy(double valorBase) {
        this.valorBase = valorBase;
    }

    public double getValorBase() {
        return valorBase;
    }

    // Cada estratégia DEVE implementar como o cálculo é feito
    public abstract double calcular(Conta conta);
}