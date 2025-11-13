package br.univali.cc.prog3.banco.dominio;

import br.univali.cc.prog3.banco.utilitario.ValidaCPF;

public class Cliente {
    private String nome;
    private String cpf;

    public Cliente(String nome, String cpf) {
        if (!ValidaCPF.validarCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        this.nome = nome;
        this.cpf = cpf;
    }

    public String obter_nome() {
        return nome;
    }

    public String obter_cpf() {
        return cpf;
    }

    public String obter_cpf_formatado() {
        return ValidaCPF.formatarCPF(cpf);
    }

    @Override
    public String toString() {
        return nome + " (CPF: " + obter_cpf_formatado() + ")";
    }
}