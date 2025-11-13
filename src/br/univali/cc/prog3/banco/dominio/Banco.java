package br.univali.cc.prog3.banco.dominio;

import br.univali.cc.prog3.banco.excecao.ContaDuplicadaException;
import br.univali.cc.prog3.banco.excecao.ContaInexistenteException;
import br.univali.cc.prog3.banco.excecao.SaldoInsuficienteException;
import br.univali.cc.prog3.banco.excecao.ValorNegativoException;
import java.util.HashMap;
import java.util.Map;

public class Banco {
    private String nome;
    private String slogan;
    private int numero;
    private Map<Integer, ContaBancaria> contas;
    private int numero_conta_sequencial;

    public Banco(String nome, String slogan, int numero) {
        this.nome = nome;
        this.slogan = slogan;
        this.numero = numero;
        this.contas = new HashMap<>();
        this.numero_conta_sequencial = 1;
    }

    public void criar_conta(int numero_conta, double saldo_inicial, Cliente cliente)
            throws ContaDuplicadaException, ValorNegativoException {
        if (saldo_inicial < 0) {
            throw new ValorNegativoException("Saldo inicial não pode ser negativo");
        }
        if (contas.containsKey(numero_conta)) {
            throw new ContaDuplicadaException("Já existe uma conta com o número " + numero_conta);
        }
        ContaCorrente nova_conta = new ContaCorrente(numero_conta, saldo_inicial, cliente);
        contas.put(numero_conta, nova_conta);
    }

    public void criar_conta(int numero_conta, double saldo_inicial, double limite, Cliente cliente)
            throws ContaDuplicadaException, ValorNegativoException {
        if (saldo_inicial < 0 || limite < 0) {
            throw new ValorNegativoException("Saldo inicial e limite não podem ser negativos");
        }
        if (contas.containsKey(numero_conta)) {
            throw new ContaDuplicadaException("Já existe uma conta com o número " + numero_conta);
        }
        ContaCorrente nova_conta = new ContaCorrente(numero_conta, saldo_inicial, cliente, limite);
        contas.put(numero_conta, nova_conta);
    }

    private ContaBancaria localizar_conta(int numero) throws ContaInexistenteException {
        ContaBancaria conta = contas.get(numero);
        if (conta == null) {
            throw new ContaInexistenteException("Conta " + numero + " não encontrada");
        }
        return conta;
    }

    public void depositar(int numero, double valor)
            throws ContaInexistenteException, ValorNegativoException {
        if (valor <= 0) {
            throw new ValorNegativoException("Valor para depósito deve ser positivo");
        }
        ContaBancaria conta = localizar_conta(numero);
        conta.depositar(valor);
    }

    public void sacar(int numero, double valor)
            throws ContaInexistenteException, ValorNegativoException, SaldoInsuficienteException {
        if (valor <= 0) {
            throw new ValorNegativoException("Valor para saque deve ser positivo");
        }
        ContaBancaria conta = localizar_conta(numero);
        if (!conta.sacar(valor)) {
            throw new SaldoInsuficienteException("Saldo insuficiente para saque de R$ " + String.format("%.2f", valor));
        }
    }

    public void transferir(int numero_origem, int numero_destino, double valor)
            throws ContaInexistenteException, ValorNegativoException, SaldoInsuficienteException {
        if (valor <= 0) {
            throw new ValorNegativoException("Valor para transferência deve ser positivo");
        }
        ContaBancaria origem = localizar_conta(numero_origem);
        ContaBancaria destino = localizar_conta(numero_destino);
        if (!origem.sacar(valor)) {
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência de R$ " + String.format("%.2f", valor));
        }
        destino.depositar(valor);
    }

    public String emitir_extrato(int numero) throws ContaInexistenteException {
        ContaBancaria conta = localizar_conta(numero);
        return conta.emitir_extrato();
    }

    public String obter_nome() {
        return nome;
    }

    public String obter_slogan() {
        return slogan;
    }

    public int obter_numero() {
        return numero;
    }

    public Map<Integer, ContaBancaria> obter_contas() {
        return new HashMap<>(contas);
    }
}
