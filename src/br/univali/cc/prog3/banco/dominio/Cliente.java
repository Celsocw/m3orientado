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
    
    public String getNome() {
        return nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public String getCpfFormatado() {
        return ValidaCPF.formatarCPF(cpf);
    }
    
    @Override
    public String toString() {
        return nome + " (CPF: " + getCpfFormatado() + ")";
    }
}