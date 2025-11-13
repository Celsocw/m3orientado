package br.univali.cc.prog3.banco.excecao;

public class ContaInexistenteException extends BancoException {
    public ContaInexistenteException(String message) {
        super(message);
    }
}