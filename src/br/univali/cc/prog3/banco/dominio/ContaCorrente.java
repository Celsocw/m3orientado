package br.univali.cc.prog3.banco.dominio;

public class ContaCorrente extends ContaBancaria {
    private double limite;
    private boolean tem_limite;

    public ContaCorrente(int numero, double saldo_inicial, Cliente titular) {
        super(numero, saldo_inicial, titular);
        this.limite = 0;
        this.tem_limite = false;
    }

    public ContaCorrente(int numero, double saldo_inicial, Cliente titular, double limite) {
        super(numero, saldo_inicial, titular);
        this.limite = limite;
        this.tem_limite = true;
    }

    public double obter_limite() {
        return limite;
    }

    public boolean tem_limite() {
        return tem_limite;
    }

    @Override
    protected boolean pode_sacar(double valor) {
        return saldo + limite >= valor;
    }

    @Override
    protected String obter_tipo_conta() {
        return tem_limite ? "CONTA CORRENTE COM LIMITE" : "CONTA CORRENTE";
    }

    @Override
    public String emitir_extrato() {
        String extrato = super.emitir_extrato();
        if (tem_limite) {
            extrato += "\nLIMITE DISPONÍVEL: R$ " + String.format("%.2f", limite);
        }
        return extrato;
    }
}