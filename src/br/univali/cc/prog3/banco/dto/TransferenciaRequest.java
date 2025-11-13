package br.univali.cc.prog3.banco.dto;

public class TransferenciaRequest {
    private int numeroOrigem;
    private int numeroDestino;
    private double valor;

    public TransferenciaRequest() {}

    public TransferenciaRequest(int numeroOrigem, int numeroDestino, double valor) {
        this.numeroOrigem = numeroOrigem;
        this.numeroDestino = numeroDestino;
        this.valor = valor;
    }

    public int getNumeroOrigem() {
        return numeroOrigem;
    }

    public void setNumeroOrigem(int numeroOrigem) {
        this.numeroOrigem = numeroOrigem;
    }

    public int getNumeroDestino() {
        return numeroDestino;
    }

    public void setNumeroDestino(int numeroDestino) {
        this.numeroDestino = numeroDestino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}