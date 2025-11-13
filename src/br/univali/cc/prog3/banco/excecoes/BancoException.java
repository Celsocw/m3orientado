package br.univali.cc.prog3.banco.excecoes;

public class BancoException extends Exception {
    public BancoException(String message) {
        super(message);
    }
    
    public BancoException(String message, Throwable cause) {
        super(message, cause);
    }
}