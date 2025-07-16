package org.example;

import java.util.*;

public class ArvoreDNS {
    private final Dominio raiz;

    public ArvoreDNS() {
        raiz = new Dominio("raiz", 0, "root");
    }

    // método para inserir domínio/subdomínio
    public void inserirDominio(String dominioCompleto, int ttl, String tipo) {
        String[] partes = dominioCompleto.split("\\.");
        Dominio atual = raiz;

        for (int i = partes.length - 1; i >= 0; i--) {
            String parte = partes[i];
            atual.getFilhos().putIfAbsent(parte, new Dominio(parte, ttl, tipo));
            atual = atual.getFilhos().get(parte);
        }
    }

    // mostrar a árvore com indentação
    public void exibirArvore() {
        exibirRecursivo(raiz, "");
    }

    private void exibirRecursivo(Dominio atual, String prefixo) {
        if (!atual.getNome().equals("raiz")) {
            System.out.println(prefixo + atual.getNome());
            prefixo += "  ";
        }
        for (Dominio filho : atual.getFilhos().values()) {
            exibirRecursivo(filho, prefixo);
        }
    }

    // buscar domínio
    public boolean buscarDominio(String dominioCompleto) {
        String[] partes = dominioCompleto.split("\\.");
        Dominio atual = raiz;

        for (int i = partes.length - 1; i >= 0; i--) {
            String parte = partes[i];
            if (!atual.getFilhos().containsKey(parte)) {
                return false;
            }
            atual = atual.getFilhos().get(parte);
        }

        System.out.println("Domínio encontrado: " + atual.getNome());
        System.out.println("TTL: " + atual.getTtl());
        System.out.println("Tipo: " + atual.getTipo());
        return true;
    }

    // remover domínio e subdomínios
    public boolean removerDominio(String dominioCompleto) {
        return removerRecursivo(raiz, dominioCompleto.split("\\."), dominioCompleto.split("\\.").length - 1);
    }

    private boolean removerRecursivo(Dominio atual, String[] partes, int index) {
        if (index < 0) return false;

        String parte = partes[index];
        if (!atual.getFilhos().containsKey(parte)) return false;

        if (index == 0) {
            atual.removerFilho(parte);
            return true;
        }

        Dominio proximo = atual.getFilhos().get(parte);
        return removerRecursivo(proximo, partes, index - 1);
    }

    // exportar todos os caminhos completos (zonas)
    public void exportarZonas() {
        exportarRecursivo(raiz, "");
    }

    private void exportarRecursivo(Dominio atual, String caminho) {
        String novoCaminho = atual.getNome().equals("raiz") ? "" : atual.getNome() + (caminho.isEmpty() ? "" : "." + caminho);
        if (!atual.getNome().equals("raiz")) {
            System.out.println("Domínio: " + novoCaminho + " | TTL: " + atual.getTtl() + " | Tipo: " + atual.getTipo());
        }

        for (Dominio filho : atual.getFilhos().values()) {
            exportarRecursivo(filho, novoCaminho);
        }
    }
}