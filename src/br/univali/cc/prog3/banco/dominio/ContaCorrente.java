package br.univali.cc.prog3.banco.dominio;

public class ContaCorrente extends ContaBancaria {
    private double limite;
    private boolean temLimite;
    
    public ContaCorrente(int numero, double saldoInicial, Cliente titular) {
        super(numero, saldoInicial, titular);
        this.limite = 0;
        this.temLimite = false;
    }
    
    public ContaCorrente(int numero, double saldoInicial, Cliente titular, double limite) {
        super(numero, saldoInicial, titular);
        this.limite = limite;
        this.temLimite = true;
    }
    
    public double getLimite() {
        return limite;
    }
    
    public boolean temLimite() {
        return temLimite;
    }
    
    @Override
    protected boolean podeSacar(double valor) {
        return saldo + limite >= valor;
    }
    
    @Override
    protected String getTipoConta() {
        return temLimite ? "CONTA CORRENTE COM LIMITE" : "CONTA CORRENTE";
    }
    
    @Override
    public String emitirExtrato() {
        String extrato = super.emitirExtrato();
        if (temLimite) {
            extrato += "\nLIMITE DISPONÍVEL: R$ " + String.format("%.2f", limite);
        }
        return extrato;
    }
}