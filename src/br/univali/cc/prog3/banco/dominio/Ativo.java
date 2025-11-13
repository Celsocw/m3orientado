/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.univali.cc.prog3.banco.dominio;

/**
 *
 * @author 1978233
 */
public class Ativo {
    private String codigo;
    private String nome;
    private double precoAtual;
    private double precoAnterior;
    private double variacao;

    public Ativo(String codigo, String nome, double precoAtual, double precoAnterior) {
        this.codigo = codigo;
        this.nome = nome;
        this.precoAtual = precoAtual;
        this.precoAnterior = precoAnterior;
        this.variacao = ((precoAtual - precoAnterior) / precoAnterior) * 100;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public double getPrecoAtual() {
        return precoAtual;
    }

    public double getPrecoAnterior() {
        return precoAnterior;
    }

    public double getVariacao() {
        return variacao;
    }

    @Override
    public String toString() {
        return String.format("%s - %s: R$ %.2f (Var: %.2f%%)", 
                codigo, nome, precoAtual, variacao);
    }
}