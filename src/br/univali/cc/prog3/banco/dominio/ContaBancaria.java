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

    public ContaBancaria(int numero, double saldo_inicial, Cliente titular) {
        this.numero = numero;
        this.saldo = saldo_inicial;
        this.titular = titular;
        this.extrato = new ArrayList<>();
        if (saldo_inicial > 0) {
            adicionar_extrato("Saldo inicial", saldo_inicial, 'C');
        }
    }

    public int obter_numero() {
        return numero;
    }

    public double obter_saldo() {
        return saldo;
    }

    public Cliente obter_titular() {
        return titular;
    }

    public boolean depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            adicionar_extrato("Depósito", valor, 'C');
            return true;
        }
        return false;
    }

    public boolean sacar(double valor) {
        if (valor > 0 && pode_sacar(valor)) {
            saldo -= valor;
            adicionar_extrato("Saque", valor, 'D');
            return true;
        }
        return false;
    }

    protected abstract boolean pode_sacar(double valor);

    protected void adicionar_extrato(String descricao, double valor, char tipo) {
        extrato.add(new ExtratoEntry(descricao, valor, tipo));
    }

    public String emitir_extrato() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EXTRATO DA CONTA ").append(numero).append(" ===\n");
        sb.append("Titular: ").append(titular.obter_nome()).append("\n");
        sb.append("CPF: ").append(titular.obter_cpf_formatado()).append("\n");
        sb.append("Tipo: ").append(obter_tipo_conta()).append("\n\n");
        List<ExtratoEntry> movimentacoes_ordenadas = obter_movimentacoes_ordenadas();
        for (ExtratoEntry entrada : movimentacoes_ordenadas) {
            sb.append(entrada.toString()).append("\n");
        }
        sb.append("\nSALDO ATUAL: R$ ").append(String.format("%.2f", saldo));
        return sb.toString();
    }

    public List<ExtratoEntry> obter_movimentacoes_ordenadas() {
        List<ExtratoEntry> movimentacoes_ordenadas = new ArrayList<>(extrato);
        Collections.sort(movimentacoes_ordenadas, new Comparator<ExtratoEntry>() {
            @Override
            public int compare(ExtratoEntry e1, ExtratoEntry e2) {
                return e2.obter_data_hora().compareTo(e1.obter_data_hora());
            }
        });
        return movimentacoes_ordenadas;
    }

    protected abstract String obter_tipo_conta();

    public boolean transferir(ContaBancaria destino, double valor) {
        if (this.sacar(valor)) {
            destino.depositar(valor);
            adicionar_extrato("Transferência para conta " + destino.obter_numero(), valor, 'D');
            return true;
        }
        return false;
    }
}