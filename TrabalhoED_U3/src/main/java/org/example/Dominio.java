package org.example;

import java.util.HashMap;
import java.util.Map;

public class Dominio {
    private String nome;
    private int ttl;
    private String tipo;
    private Map<String, Dominio> filhos;

    public Dominio(String nome, int ttl, String tipo) {
        this.nome = nome;
        this.ttl = ttl;
        this.tipo = tipo;
        this.filhos = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public int getTtl() {
        return ttl;
    }

    public String getTipo() {
        return tipo;
    }

    public Map<String, Dominio> getFilhos() {
        return filhos;
    }

    public void adicionarFilho(String nome, Dominio filho) {
        filhos.put(nome, filho);
    }

    public void removerFilho(String nome) {
        filhos.remove(nome);
    }
}
