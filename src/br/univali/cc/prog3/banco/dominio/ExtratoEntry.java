package br.univali.cc.prog3.banco.dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtratoEntry {
    private LocalDateTime dataHora;
    private String descricao;
    private double valor;
    private char tipo;
    
    public ExtratoEntry(String descricao, double valor, char tipo) {
        this.dataHora = LocalDateTime.now();
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public double getValor() {
        return valor;
    }
    
    public char getTipo() {
        return tipo;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataFormatada = dataHora.format(formatter);
        String tipoStr = (tipo == 'C') ? "C" : "D";
        return String.format("[%s] (%s) %s R$ %.2f", dataFormatada, tipoStr, descricao, valor);
    }
}