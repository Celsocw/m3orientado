/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.univali.cc.prog3.banco.dominio;

import java.util.Random;

/**
 *
 * @author 1978233
 */
public class AtivosAPI {
    private static final String[] CODIGOS = {"PETR4", "VALE3", "ITUB4", "BBDC4", "ABEV3", "WEGE3", "MGLU3", "B3SA3"};
    private static final String[] NOMES = {
        "Petrobras", "Vale", "Itaú Unibanco", "Bradesco", "Ambev", 
        "WEG", "Magazine Luiza", "B3"
    };
    
    private static Random random = new Random();
    
    public static Ativo[] getAtivos() {
        Ativo[] ativos = new Ativo[CODIGOS.length];
        
        for (int i = 0; i < CODIGOS.length; i++) {
            double precoAnterior = 10.0 + random.nextDouble() * 90.0;
            double variacao = (random.nextDouble() - 0.5) * 0.2;
            double precoAtual = precoAnterior * (1 + variacao);
            
            ativos[i] = new Ativo(CODIGOS[i], NOMES[i], precoAtual, precoAnterior);
        }
        
        return ativos;
    }
    
    public static Ativo getAtivoPorCodigo(String codigo) {
        Ativo[] ativos = getAtivos();
        for (Ativo ativo : ativos) {
            if (ativo.getCodigo().equals(codigo)) {
                return ativo;
            }
        }
        return null;
    }
}