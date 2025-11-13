package br.univali.cc.prog3.banco.dominio;

import java.time.LocalDateTime;

public class Usuario {
    private String username;
    private String senha;
    private String nome;
    private String cpf;
    private LocalDateTime dataCriacao;
    private boolean ativo;
    
    public Usuario(String username, String senha, String nome, String cpf) {
        this.username = username;
        this.senha = senha;
        this.nome = nome;
        this.cpf = cpf;
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }
    
    public boolean autenticar(String senha) {
        return this.senha.equals(senha) && this.ativo;
    }
    
    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
    }
    
    public void desativar() {
        this.ativo = false;
    }
    
    public void ativar() {
        this.ativo = true;
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public boolean isAtivo() { return ativo; }
}