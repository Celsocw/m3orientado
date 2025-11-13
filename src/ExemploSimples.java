import br.univali.cc.prog3.banco.dominio.Banco;
import br.univali.cc.prog3.banco.dominio.Cliente;
import br.univali.cc.prog3.banco.excecao.*;

/**
 * Exemplo simples para testar o sistema bancário
 */
public class ExemploSimples {

    public static void main(String[] args) {
        System.out.println("=== TESTE SIMPLES DO SISTEMA BANCÁRIO ===\n");

        try {
            // Criar banco
            Banco banco = new Banco("Banco Teste", "Slogan", 1);

            // Criar cliente
            Cliente cliente = new Cliente("João Silva", "12345678901");

            // Criar conta
            banco.criarConta(1001, 1000.0, cliente);
            System.out.println("Conta criada com sucesso!");

            // Fazer operações
            banco.depositar(1001, 500.0);
            System.out.println("Depósito realizado!");

            banco.sacar(1001, 200.0);
            System.out.println("Saque realizado!");

            // Emitir extrato
            String extrato = banco.emitirExtrato(1001);
            System.out.println("\n" + extrato);

            // Testar erro - conta duplicada
            try {
                banco.criarConta(1001, 500.0, cliente);
            } catch (ContaDuplicadaException e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }

            // Testar erro - valor negativo
            try {
                banco.sacar(1001, -100.0);
            } catch (ValorNegativoException e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }

            // Testar erro - saldo insuficiente
            try {
                banco.sacar(1001, 10000.0);
            } catch (SaldoInsuficienteException e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }

            System.out.println("\n=== TODOS OS TESTES PASSARAM! ===");

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
