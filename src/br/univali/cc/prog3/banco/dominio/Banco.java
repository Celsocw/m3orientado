package br.univali.cc.prog3.banco.dominio;

import br.univali.cc.prog3.banco.excecao.BancoException;
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
    private int numeroContaSequencial;

    public Banco(String nome, String slogan, int numero) {
        this.nome = nome;
        this.slogan = slogan;
        this.numero = numero;
        this.contas = new HashMap<>();
        this.numeroContaSequencial = 1;
    }

    /**
     * Cria uma conta corrente sem limite especial
     * @param numeroConta Número da conta a ser criado
     * @param saldoInicial Saldo inicial da conta
     * @param cliente Titular da conta
     * @throws ContaDuplicadaException se já existir uma conta com o mesmo número
     * @throws ValorNegativoException se o saldo inicial for negativo
     */
    public void criarConta(int numeroConta, double saldoInicial, Cliente cliente)
            throws ContaDuplicadaException, ValorNegativoException {

        // 1. Descobrir o erro - verificar se valor é negativo
        if (saldoInicial < 0) {
            // 2. Lançar a exceção
            throw new ValorNegativoException("Saldo inicial não pode ser negativo");
        }

        // 1. Descobrir o erro - verificar se conta já existe
        if (contas.containsKey(numeroConta)) {
            // 2. Lançar a exceção
            throw new ContaDuplicadaException("Já existe uma conta com o número " + numeroConta);
        }

        // Criar conta corrente sem limite
        ContaCorrente novaConta = new ContaCorrente(numeroConta, saldoInicial, cliente);
        contas.put(numeroConta, novaConta);
    }

    /**
     * Cria uma conta corrente com limite especial
     * @param numeroConta Número da conta a ser criado
     * @param saldoInicial Saldo inicial da conta
     * @param limite Limite de crédito da conta
     * @param cliente Titular da conta
     * @throws ContaDuplicadaException se já existir uma conta com o mesmo número
     * @throws ValorNegativoException se o saldo inicial ou limite for negativo
     */
    public void criarConta(int numeroConta, double saldoInicial, double limite, Cliente cliente)
            throws ContaDuplicadaException, ValorNegativoException {

        // 1. Descobrir o erro - verificar se valores são negativos
        if (saldoInicial < 0 || limite < 0) {
            // 2. Lançar a exceção
            throw new ValorNegativoException("Saldo inicial e limite não podem ser negativos");
        }

        // 1. Descobrir o erro - verificar se conta já existe
        if (contas.containsKey(numeroConta)) {
            // 2. Lançar a exceção
            throw new ContaDuplicadaException("Já existe uma conta com o número " + numeroConta);
        }

        // Criar conta corrente com limite
        ContaCorrente novaConta = new ContaCorrente(numeroConta, saldoInicial, cliente, limite);
        contas.put(numeroConta, novaConta);
    }

    /**
     * Localiza uma conta pelo número
     * @param numero Número da conta
     * @return ContaBancaria encontrada
     * @throws ContaInexistenteException se a conta não existir
     */
    private ContaBancaria localizarConta(int numero) throws ContaInexistenteException {
        ContaBancaria conta = contas.get(numero);
        if (conta == null) {
            throw new ContaInexistenteException("Conta " + numero + " não encontrada");
        }
        return conta;
    }

    /**
     * Realiza depósito em uma conta
     * @param numero Número da conta
     * @param valor Valor a ser depositado
     * @throws ContaInexistenteException se a conta não existir
     * @throws ValorNegativoException se o valor for negativo
     */
    public void depositar(int numero, double valor)
            throws ContaInexistenteException, ValorNegativoException {

        // 1. Descobrir o erro - verificar se valor é negativo
        if (valor <= 0) {
            // 2. Lançar a exceção
            throw new ValorNegativoException("Valor para depósito deve ser positivo");
        }

        ContaBancaria conta = localizarConta(numero);
        conta.depositar(valor);
    }

    /**
     * Realiza saque em uma conta
     * @param numero Número da conta
     * @param valor Valor a ser sacado
     * @throws ContaInexistenteException se a conta não existir
     * @throws ValorNegativoException se o valor for negativo
     * @throws SaldoInsuficienteException se não houver saldo suficiente
     */
    public void sacar(int numero, double valor)
            throws ContaInexistenteException, ValorNegativoException, SaldoInsuficienteException {

        // 1. Descobrir o erro - verificar se valor é negativo
        if (valor <= 0) {
            // 2. Lançar a exceção
            throw new ValorNegativoException("Valor para saque deve ser positivo");
        }

        ContaBancaria conta = localizarConta(numero);

        // 1. Descobrir o erro - verificar se há saldo suficiente
        if (!conta.sacar(valor)) {
            // 2. Lançar a exceção
            throw new SaldoInsuficienteException("Saldo insuficiente para saque de R$ " + String.format("%.2f", valor));
        }
    }

    /**
     * Realiza transferência entre contas
     * @param numeroOrigem Número da conta de origem
     * @param numeroDestino Número da conta de destino
     * @param valor Valor a ser transferido
     * @throws ContaInexistenteException se uma das contas não existir
     * @throws ValorNegativoException se o valor for negativo
     * @throws SaldoInsuficienteException se não houver saldo suficiente na conta de origem
     */
    public void transferir(int numeroOrigem, int numeroDestino, double valor)
            throws ContaInexistenteException, ValorNegativoException, SaldoInsuficienteException {

        // 1. Descobrir o erro - verificar se valor é negativo
        if (valor <= 0) {
            // 2. Lançar a exceção
            throw new ValorNegativoException("Valor para transferência deve ser positivo");
        }

        // Verificar se contas existem
        ContaBancaria origem = localizarConta(numeroOrigem);
        ContaBancaria destino = localizarConta(numeroDestino);

        // 1. Descobrir o erro - verificar se há saldo suficiente na origem
        if (!origem.sacar(valor)) {
            // 2. Lançar a exceção
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência de R$ " + String.format("%.2f", valor));
        }

        // Se saque foi bem-sucedido, depositar no destino
        destino.depositar(valor);
    }

    /**
     * Emite extrato de uma conta
     * @param numero Número da conta
     * @return String com o extrato da conta
     * @throws ContaInexistenteException se a conta não existir
     */
    public String emitirExtrato(int numero) throws ContaInexistenteException {
        ContaBancaria conta = localizarConta(numero);
        return conta.emitirExtrato();
    }

    public String getNome() {
        return nome;
    }

    public String getSlogan() {
        return slogan;
    }

    public int getNumero() {
        return numero;
    }

    public Map<Integer, ContaBancaria> getContas() {
        return new HashMap<>(contas); // Retorna cópia para evitar modificações externas
    }
}
