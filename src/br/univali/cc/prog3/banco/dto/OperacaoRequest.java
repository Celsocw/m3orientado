package br.univali.cc.prog3.banco.dto;

public class OperacaoRequest {
    private int numero;
    private double valor;

    public OperacaoRequest() {}

    public OperacaoRequest(int numero, double valor) {
        this.numero = numero;
        this.valor = valor;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}