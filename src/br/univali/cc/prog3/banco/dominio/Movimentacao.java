package br.univali.cc.prog3.banco.dominio;

import java.time.LocalDateTime;

public class Movimentacao {
    private String descricao;
    private char tipo; //D C
    private double valor;
    private LocalDateTime dataHora;
    private double saldoAposMovimentacao;

    public Movimentacao(String descricao, char tipo, double valor, double saldoAposMovimentacao) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
        this.saldoAposMovimentacao = saldoAposMovimentacao;
    }
    
    public String getMovimentacao(){
        return String.format("[%s] (%c) %s R$%.2f | Saldo: R$%.2f", 
                dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                tipo, descricao, valor, saldoAposMovimentacao);
    }
    
    // Getters
    public String getDescricao() { return descricao; }
    public char getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public double getSaldoAposMovimentacao() { return saldoAposMovimentacao; }
}
