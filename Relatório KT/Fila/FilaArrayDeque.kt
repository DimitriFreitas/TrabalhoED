fun main() {
    val fila: ArrayDeque<String> = ArrayDeque()

    fila.addLast("João")
    fila.addLast("Maria")
    fila.addLast("Carlos")

    println("Primeiro da fila: ${fila.first()}")

    while (fila.isNotEmpty()) {
        println("Removido: ${fila.removeFirst()}")
    }
}