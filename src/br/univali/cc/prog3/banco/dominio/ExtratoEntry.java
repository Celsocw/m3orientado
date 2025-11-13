package br.univali.cc.prog3.banco.dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtratoEntry {
    private LocalDateTime data_hora;
    private String descricao;
    private double valor;
    private char tipo;

    public ExtratoEntry(String descricao, double valor, char tipo) {
        this.data_hora = LocalDateTime.now();
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
    }

    public LocalDateTime obter_data_hora() {
        return data_hora;
    }

    public String obter_descricao() {
        return descricao;
    }

    public double obter_valor() {
        return valor;
    }

    public char obter_tipo() {
        return tipo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String data_formatada = data_hora.format(formatter);
        String tipo_str = (tipo == 'C') ? "C" : "D";
        return String.format("[%s] (%s) %s R$ %.2f", data_formatada, tipo_str, descricao, valor);
    }
}