package GerenciadorContaBancaria;

import java.time.LocalDateTime;

public class Movimentacao {

    private int id;
    private int numeroConta;
    private String tipo;
    private double valor;
    private LocalDateTime data;

  

    public Movimentacao(int numeroConta, String tipo, double valor) {
        this.numeroConta = numeroConta;
        this.tipo = tipo;
        this.valor = valor;
    }

  
    public int getId() { return id; }
    public int getNumeroConta() { return numeroConta; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDateTime getData() { return data; }

  
    public void setId(int id) { this.id = id; }
    public void setData(LocalDateTime data) { this.data = data; }

    @Override
    public String toString() {
        return "Movimentacao{" + "data=" + data + ", tipo=" + tipo + ", valor=" + valor + '}';
    }
}