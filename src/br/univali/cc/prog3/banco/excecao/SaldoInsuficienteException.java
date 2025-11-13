package br.univali.cc.prog3.banco.excecao;

public class SaldoInsuficienteException extends BancoException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}