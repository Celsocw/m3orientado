package br.univali.cc.prog3.banco.dto;

public class CriarContaRequest {
    private double saldoInicial;
    private Double limite;

    public CriarContaRequest() {}

    public CriarContaRequest(double saldoInicial, Double limite) {
        this.saldoInicial = saldoInicial;
        this.limite = limite;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Double getLimite() {
        return limite;
    }

    public void setLimite(Double limite) {
        this.limite = limite;
    }

    public boolean isEspecial() {
        return limite != null && limite > 0;
    }
}