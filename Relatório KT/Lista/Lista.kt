Collection<T>
    List<T>
    Set<T>
    Map<K, V>

val lista: List<String> = listOf("A", "B", "C")

val listaMutavel: MutableList<String> = mutableListOf("A", "B")
listaMutavel.add("C")
listaMutavel.remove("A")

for (i in 0 until lista.size) {
    println(lista[i])
}

for (item in lista) {
    println(item)
}

lista.forEach { println(item) }

val numeros = listOf(1, 2, 3)
val dobrados = numeros.map { it * 2 } // [2, 4, 6]

val pares = numeros.filter { it % 2 == 0 } // [2]

val lista = listOf(3, 1, 2)
val ordenada = lista.sorted() // [1, 2, 3]

val resultado = numeros.find { it > 1 } // 2

val tarefas = mutableListOf("Estudar Kotlin", "Ler livro")

fun adicionarTarefa(tarefa: String) {
    tarefas.add(tarefa)
}

fun listarTarefas() {
    tarefas.forEachIndexed { i, tarefa -> print} }

fun removerTarefa(tarefa: String) {

    if (indice in tarefas.indices) {
        tarefas.removeAt(indice)
    } 
}