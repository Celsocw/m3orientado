package br.univali.cc.prog3.banco.visao;

import br.univali.cc.prog3.banco.dominio.Banco;
import java.util.Scanner;

/**
 *
 * @author 1978233
 */
public class BancoGUI {
    private Banco banco;
    
    public String lerValor(String rotulo) {
        System.out.print(rotulo+": ");
        Scanner leitor = new Scanner(System.in);
        return leitor.nextLine();
    }

    public BancoGUI(Banco banco) {
        this.banco = banco;
    }
    
    public void menu() {
        char opcao;
        do {
            System.out.println("    ____  _            _      _   ");
            System.out.println("   | __ )| | __ _  ___| | __ | |_ ");
            System.out.println("   |  _ \\| |/ _` |/ __| |/ / | __|");
            System.out.println("   | |_) | | (_| | (__|   <  | |_ ");
            System.out.println("   |____/|_|\\__,_|\\___|_|\\_\\  \\__|");
            System.out.println();
            System.out.println("   " + this.banco.getNome());
            System.out.println("   " + this.banco.getSlogan());
            System.out.println();
            System.out.println("Menu do "+this.banco.getNome());
            System.out.println("1 - Criar conta simples");
            System.out.println("2 - Criar conta especial");
            System.out.println("3 - Depositar");
            System.out.println("4 - Sacar");
            System.out.println("5 - Transferir");
            System.out.println("6 - Extrato");
            System.out.println("S - Sair");
            opcao = this.lerValor("Selecione uma opção").toUpperCase().charAt(0);
            switch (opcao) {
                case '1': criarContaSimples();break;
                case '2': criarContaEspecial();break;
                case '3': depositar();break;
                case '4': sacar();break;
                case '5': transferir();break;
                case '6': extrato();break;
                case 'S': System.out.println("Saindo do sistema..."); break;
                default: System.out.println("Opção inválida!"); break;
            }
        } while (opcao != 'S');
    }

    private void criarContaSimples() {
        try {
            double saldoInicial = Double.parseDouble(lerValor("Informe o saldo inicial"));
            this.banco.criarConta(saldoInicial);
            System.out.println("Conta simples criada com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Informe um número válido.");
        }
    }
    
    private void criarContaEspecial() {
        try {
            double saldoInicial = Double.parseDouble(lerValor("Informe o saldo inicial"));
            double limite = Double.parseDouble(lerValor("Informe o limite da conta"));
            this.banco.criarConta(saldoInicial, limite);
            System.out.println("Conta especial criada com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Informe um número válido.");
        }
    }
    
    private void depositar() {
        try {
            int numero = Integer.parseInt(lerValor("Informe o numero da conta"));
            double valor = Double.parseDouble(lerValor("Informe o valor para depósito"));
            this.banco.depositar(numero, valor);
            System.out.println("Depósito realizado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Informe um número válido.");
        }
    }
    
    private void sacar() {
        try {
            int numero = Integer.parseInt(lerValor("Informe o numero da conta"));
            double valor = Double.parseDouble(lerValor("Informe o valor para saque"));
            boolean sucesso = this.banco.sacar(numero, valor);
            if (sucesso) {
                System.out.println("Saque realizado com sucesso!");
            } else {
                System.out.println("Saldo insuficiente ou conta não encontrada!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Informe um número válido.");
        }
    }
    
    private void transferir() {
        try {
            int numeroOrigem = Integer.parseInt(lerValor("Informe o numero da conta de origem"));
            int numeroDestino = Integer.parseInt(lerValor("Informe o numero da conta de destino"));
            double valor = Double.parseDouble(lerValor("Informe o valor para transferência"));
            boolean sucesso = this.banco.transferir(numeroOrigem, numeroDestino, valor);
            if (sucesso) {
                System.out.println("Transferência realizada com sucesso!");
            } else {
                System.out.println("Transferência não realizada! Verifique os dados e o saldo.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Informe um número válido.");
        }
    }
    
    private void extrato(){
        try {
            int numero = Integer.parseInt(lerValor("Informe o numero da conta"));
            String extrato = this.banco.emitirExtrato(numero);
            if (extrato.isEmpty()) {
                System.out.println("Conta não encontrada!");
            } else {
                System.out.println(extrato);
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Informe um número válido.");
        }
    }
}
