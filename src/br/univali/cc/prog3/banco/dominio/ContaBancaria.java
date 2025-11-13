package br.univali.cc.prog3.banco.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ContaBancaria {
    protected int numero;
    protected double saldo;
    protected Cliente titular;
    protected List<ExtratoEntry> extrato;
    
    public ContaBancaria(int numero, double saldoInicial, Cliente titular) {
        this.numero = numero;
        this.saldo = saldoInicial;
        this.titular = titular;
        this.extrato = new ArrayList<>();
        if (saldoInicial > 0) {
            adicionarExtrato("Saldo inicial", saldoInicial, 'C');
        }
    }
    
    public int getNumero() {
        return numero;
    }
    
    public double getSaldo() {
        return saldo;
    }
    
    public Cliente getTitular() {
        return titular;
    }
    
    public boolean depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            adicionarExtrato("Depósito", valor, 'C');
            return true;
        }
        return false;
    }
    
    public boolean sacar(double valor) {
        if (valor > 0 && podeSacar(valor)) {
            saldo -= valor;
            adicionarExtrato("Saque", valor, 'D');
            return true;
        }
        return false;
    }
    
    protected abstract boolean podeSacar(double valor);
    
    protected void adicionarExtrato(String descricao, double valor, char tipo) {
        extrato.add(new ExtratoEntry(descricao, valor, tipo));
    }
    
    public String emitirExtrato() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EXTRATO DA CONTA ").append(numero).append(" ===\n");
        sb.append("Titular: ").append(titular.getNome()).append("\n");
        sb.append("CPF: ").append(titular.getCpfFormatado()).append("\n");
        sb.append("Tipo: ").append(getTipoConta()).append("\n\n");

        // Usar movimentações ordenadas por data (mais recente primeiro)
        List<ExtratoEntry> movimentacoesOrdenadas = getMovimentacoesOrdenadas();
        for (ExtratoEntry entry : movimentacoesOrdenadas) {
            sb.append(entry.toString()).append("\n");
        }

        sb.append("\nSALDO ATUAL: R$ ").append(String.format("%.2f", saldo));

        return sb.toString();
    }

    /**
     * Retorna uma cópia das movimentações ordenadas por data (mais recente primeiro)
     * @return Lista ordenada das movimentações
     */
    public List<ExtratoEntry> getMovimentacoesOrdenadas() {
        List<ExtratoEntry> movimentacoesOrdenadas = new ArrayList<>(extrato);
        Collections.sort(movimentacoesOrdenadas, new Comparator<ExtratoEntry>() {
            @Override
            public int compare(ExtratoEntry e1, ExtratoEntry e2) {
                return e2.getDataHora().compareTo(e1.getDataHora()); // Mais recente primeiro
            }
        });
        return movimentacoesOrdenadas;
    }
    
    protected abstract String getTipoConta();
    
    public boolean transferir(ContaBancaria destino, double valor) {
        if (this.sacar(valor)) {
            destino.depositar(valor);
            adicionarExtrato("Transferência para conta " + destino.getNumero(), valor, 'D');
            return true;
        }
        return false;
    }
}