import br.univali.cc.prog3.banco.dominio.Banco;
import br.univali.cc.prog3.banco.dominio.Cliente;
import br.univali.cc.prog3.banco.excecao.*;

public class TesteSistemaBancario {
    public static void main(String[] args) {
        System.out.println("=== TESTE DO SISTEMA BANCÁRIO ===\n");
        Banco banco = new Banco("Banco do Brasil", "Feito para você", 1);

        try {
            Cliente cliente1 = new Cliente("João Silva", "12345678901");
            Cliente cliente2 = new Cliente("Maria Santos", "98765432100");

            System.out.println("1. Testando criação de contas...");
            banco.criar_conta(1001, 1000.0, cliente1);
            System.out.println("   ✓ Conta 1001 criada com sucesso para " + cliente1.obter_nome());
            banco.criar_conta(1002, 500.0, cliente2);
            System.out.println("   ✓ Conta 1002 criada com sucesso para " + cliente2.obter_nome());

            System.out.println("   ✗ Tentando criar conta duplicada...");
            try {
                banco.criar_conta(1001, 200.0, cliente2);
                System.out.println("   ERRO: Conta duplicada foi criada!");
            } catch (ContaDuplicadaException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("   ✗ Tentando criar conta com saldo negativo...");
            try {
                banco.criar_conta(1003, -100.0, cliente1);
                System.out.println("   ERRO: Conta com saldo negativo foi criada!");
            } catch (ValorNegativoException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("\n2. Testando operações bancárias...");
            banco.depositar(1001, 500.0);
            System.out.println("   ✓ Depósito de R$ 500,00 realizado na conta 1001");

            System.out.println("   ✗ Tentando depositar valor negativo...");
            try {
                banco.depositar(1001, -100.0);
                System.out.println("   ERRO: Depósito negativo foi realizado!");
            } catch (ValorNegativoException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            banco.sacar(1001, 200.0);
            System.out.println("   ✓ Saque de R$ 200,00 realizado na conta 1001");

            System.out.println("   ✗ Tentando sacar valor maior que saldo...");
            try {
                banco.sacar(1001, 2000.0);
                System.out.println("   ERRO: Saque sem saldo foi realizado!");
            } catch (SaldoInsuficienteException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("   ✗ Tentando sacar valor negativo...");
            try {
                banco.sacar(1001, -50.0);
                System.out.println("   ERRO: Saque negativo foi realizado!");
            } catch (ValorNegativoException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            banco.transferir(1001, 1002, 300.0);
            System.out.println("   ✓ Transferência de R$ 300,00 da conta 1001 para 1002 realizada");

            System.out.println("   ✗ Tentando transferir de conta inexistente...");
            try {
                banco.transferir(9999, 1002, 100.0);
                System.out.println("   ERRO: Transferência de conta inexistente foi realizada!");
            } catch (ContaInexistenteException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("   ✗ Tentando transferir para conta inexistente...");
            try {
                banco.transferir(1001, 9999, 100.0);
                System.out.println("   ERRO: Transferência para conta inexistente foi realizada!");
            } catch (ContaInexistenteException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("   ✗ Tentando transferir valor negativo...");
            try {
                banco.transferir(1001, 1002, -50.0);
                System.out.println("   ERRO: Transferência negativa foi realizada!");
            } catch (ValorNegativoException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("   ✗ Tentando transferir valor maior que saldo...");
            try {
                banco.transferir(1001, 1002, 2000.0);
                System.out.println("   ERRO: Transferência sem saldo foi realizada!");
            } catch (SaldoInsuficienteException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("\n3. Testando emissão de extratos...");
            String extrato1001 = banco.emitir_extrato(1001);
            System.out.println("   ✓ Extrato da conta 1001:");
            System.out.println(extrato1001);
            String extrato1002 = banco.emitir_extrato(1002);
            System.out.println("   ✓ Extrato da conta 1002:");
            System.out.println(extrato1002);

            System.out.println("   ✗ Tentando emitir extrato de conta inexistente...");
            try {
                banco.emitir_extrato(9999);
                System.out.println("   ERRO: Extrato de conta inexistente foi emitido!");
            } catch (ContaInexistenteException e) {
                System.out.println("   ✓ Exceção lançada corretamente: " + e.getMessage());
            }

            System.out.println("\n=== TODOS OS TESTES FORAM EXECUTADOS COM SUCESSO! ===");

        } catch (Exception e) {
            System.out.println("ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
