package org.example;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArvoreDNS dns = new ArvoreDNS();
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== SISTEMA DNS ===");
            System.out.println("1 - Inserir domínio");
            System.out.println("2 - Remover domínio");
            System.out.println("3 - Buscar domínio");
            System.out.println("4 - Exibir árvore");
            System.out.println("5 - Exportar zonas");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Digite o domínio (ex: app.example.com): ");
                    String dominio = sc.nextLine();
                    System.out.print("TTL: ");
                    int ttl = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Tipo: ");
                    String tipo = sc.nextLine();
                    dns.inserirDominio(dominio, ttl, tipo);
                    break;
                case 2:
                    System.out.print("Digite o domínio a remover: ");
                    String remover = sc.nextLine();
                    if (dns.removerDominio(remover)) {
                        System.out.println("Removido com sucesso.");
                    } else {
                        System.out.println("Domínio não encontrado.");
                    }
                    break;
                case 3:
                    System.out.print("Digite o domínio a buscar: ");
                    String buscar = sc.nextLine();
                    if (!dns.buscarDominio(buscar)) {
                        System.out.println("Domínio não encontrado.");
                    }
                    break;
                case 4:
                    System.out.println("Exibindo árvore:");
                    dns.exibirArvore();
                    break;
                case 5:
                    System.out.println("Exportando zonas:");
                    dns.exportarZonas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);

        sc.close();
    }
}