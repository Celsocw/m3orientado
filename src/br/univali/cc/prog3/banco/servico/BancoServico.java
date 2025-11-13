package br.univali.cc.prog3.banco.servico;

import br.univali.cc.prog3.banco.dominio.Banco;
import br.univali.cc.prog3.banco.dominio.ContaCorrente;
import br.univali.cc.prog3.banco.dto.CriarContaRequest;
import br.univali.cc.prog3.banco.dto.OperacaoRequest;
import br.univali.cc.prog3.banco.dto.TransferenciaRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BancoServico {
    private final Banco banco;
    private final Map<Integer, ContaCorrente> contasMap;

    public BancoServico() {
        this.banco = new Banco("Banco do Brasil API", 1);
        this.contasMap = new HashMap<>();
    }

    public int criarConta(CriarContaRequest request) {
        if (request.isEspecial()) {
            banco.criarConta(request.getSaldoInicial(), request.getLimite());
        } else {
            banco.criarConta(request.getSaldoInicial());
        }
        
        int numeroConta = banco.getNumeroConta();
        ContaCorrente conta = banco.localizarConta(numeroConta);
        if (conta != null) {
            contasMap.put(numeroConta, conta);
        }
        
        return numeroConta;
    }

    public boolean depositar(OperacaoRequest request) {
        if (!contasMap.containsKey(request.getNumero())) {
            return false;
        }
        
        banco.depositar(request.getNumero(), request.getValor());
        return true;
    }

    public boolean sacar(OperacaoRequest request) {
        if (!contasMap.containsKey(request.getNumero())) {
            return false;
        }
        
        banco.sacar(request.getNumero(), request.getValor());
        return true;
    }

    public boolean transferir(TransferenciaRequest request) {
        if (!contasMap.containsKey(request.getNumeroOrigem()) || 
            !contasMap.containsKey(request.getNumeroDestino())) {
            return false;
        }
        
        banco.transferir(request.getNumeroOrigem(), request.getNumeroDestino(), request.getValor());
        return true;
    }

    public String emitirExtrato(int numeroConta) {
        if (!contasMap.containsKey(numeroConta)) {
            return "Conta não encontrada";
        }
        
        return banco.emitirExtrato(numeroConta);
    }

    public double consultarSaldo(int numeroConta) {
        if (!contasMap.containsKey(numeroConta)) {
            return -1;
        }
        
        ContaCorrente conta = contasMap.get(numeroConta);
        return conta.getSaldo();
    }
}