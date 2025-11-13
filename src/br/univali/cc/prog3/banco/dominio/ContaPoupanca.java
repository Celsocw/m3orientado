package br.univali.cc.prog3.banco.dominio;

public class ContaPoupanca extends ContaBancaria {
    private static final double RENDIMENTO_MENSAL = 0.005;
    
    public ContaPoupanca(int numero, double saldoInicial, Cliente titular) {
        super(numero, saldoInicial, titular);
    }
    
    public void aplicarRendimentoMensal() {
        if (saldo > 0) {
            double rendimento = saldo * RENDIMENTO_MENSAL;
            saldo += rendimento;
            adicionar_extrato("Rendimento mensal", rendimento, 'C');
        }
    }
    
    public double getRendimentoMensal() {
        return RENDIMENTO_MENSAL * 100;
    }
    
    @Override
    protected boolean pode_sacar(double valor) {
        return saldo >= valor;
    }
    
    @Override
    protected String obter_tipo_conta() {
        return "CONTA POUPANÇA";
    }
    
    @Override
    public String emitir_extrato() {
        String extrato = super.emitir_extrato();
        extrato += "\nTAXA DE RENDIMENTO MENSAL: " + String.format("%.2f", getRendimentoMensal()) + "%";
        return extrato;
    }
}