fun main() {
    val fila = mutableListOf("João", "Maria", "Carlos")

    println("Primeiro da fila: ${fila.first()}")

    while (fila.isNotEmpty()) {
        println("Removido: ${fila.removeAt(0)}")
    }
}