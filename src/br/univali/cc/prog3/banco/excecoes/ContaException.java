package br.univali.cc.prog3.banco.excecoes;

public class ContaException extends BancoException {
    public ContaException(String message) {
        super(message);
    }
    
    public ContaException(String message, Throwable cause) {
        super(message, cause);
    }
}