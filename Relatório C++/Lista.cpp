#include <iostream>
#include <stack>
using namespace std;

// Função auxiliar para mostrar todos os elementos da lista
void mostrarLista(const list<int>& lista) {
    cout << "Elementos da lista: ";
    for (const int& elemento : lista) {
        cout << elemento << " ";
    }
    cout << endl;   
}

int main() {
    list<int> lista;

    // Inserindo elementos no final
    lista.push_back(10);
    lista.push_back(20);
    lista.push_back(30);

    // Inserindo elementos no início
    lista.push_front(5);
    lista.push_front(1);

    // Exibir tamanho atual
    cout << "Tamanho da lista: " << lista.size() << endl;

    // Exibir primeiro e último elemento
    cout << "Primeiro elemento: " << lista.front() << endl;
    cout << "Último elemento: " << lista.back() << endl;

    mostrarLista(lista);

    // Removendo elementos do inicio e do fim
    lista.pop_front();
    lista.pop_back();

    // Inserindo em posições específicas
    auto it = lista.begin();
    advance(it, 1);
    lista.insert(it, 15);

    // Removendo elemento específico
    lista.remove(20);

    //verificar se a lista está vazia
    if (lista.empty()) {
        cout << "A lista está vazia." << endl;
    } else {
        cout << "A lista não está vazia." << endl;
    }

    return 0;
}