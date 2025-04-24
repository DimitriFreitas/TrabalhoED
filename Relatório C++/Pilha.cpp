#include <iostream>
#include <stack>
using namespace std;

// Função para mostrar os elementos da pilha
void mostrarPilha(stack<int> pilha) {
    cout << "Elementos da pilha (do topo para base): ";
    while (!pilha.empty()) {
        cout << pilha.top() << " ";
        pilha.pop();
    }
    cout << end1;
}

int main() {
    stack<int> pilha;

    //Inserindo multiplos elementos na pilha
    pilha.push(5);
    pilha.push(10);
    pilha.push(15);
    pilha.push(20);
    pilha.push(25);

    // Exibir tamanho atual
    cout << "Tamanho da pilha: " << pilha.size() << endl;

    // Exibir elemento do topo
    cout << "Elemento no topo: " << pilha.top() << endl;


    mostrarPilha(pilha);

    // Desempilhar elementos um por um
    cout << "Desempilhando elementos: ";
    while (!pilha.empty()) {
        cout << pilha.top() << " ";
        pilha.pop();
    }
    cout << endl;

    //verifica se a pilha está vazia
    if (pilha.empty()) {
        cout << "A pilha está vazia." << endl;
    }

    return 0;
}