fun <T> imprimirPilha(pilha: ArrayDeque<T>) {
        println("Elementos da pilha:")
        for (elemento in pilha.reversed()) {
            println(elemento)
        }
    }

fun main() {
    val pilhaNomes = ArrayDeque<String>()
    pilhaNomes.addLast("Alice")
    pilhaNomes.addLast("Bob")
    pilhaNomes.addLast("Carol")

    imprimirPilha(pilhaNomes)
}