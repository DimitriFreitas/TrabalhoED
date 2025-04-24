fun main() {

    val pilha = ArrayDeque<Int>();

    // Adicionando elementos
    pilha.addLast(element: 10)
    pilha.addLast(element: 20)
    pilha.addLast(element: 30)

    // Visualizando o topo
    println("Topo da pilha: {pilha.last()}") //30

    // Removendo o topo (pop)
    val removido = pilha.removeLast()
    println("Removido: $removido") //30

    // Estado atual
    println("A pilha está vazia? ${pilha.isEmpty()}")
    println("Tamanho atual: ${pilha.size}")
    println("Conteúdo atual: $pilha")

}

fun main() {

    val pilha = ArrayDeque(listOf(1, 2, 3))

}