package br.univali.cc.prog3.banco.excecoes;

public class LimiteSaqueExcedidoException extends ContaException {
    private double limite;
    private double valorSaque;
    
    public LimiteSaqueExcedidoException(String message, double limite, double valorSaque) {
        super(message);
        this.limite = limite;
        this.valorSaque = valorSaque;
    }
    
    public double getLimite() {
        return limite;
    }
    
    public double getValorSaque() {
        return valorSaque;
    }
}