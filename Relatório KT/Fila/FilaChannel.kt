import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val canal = Channel<String>()

    launch {
        canal.send("João")
        canal.send("Maria")
        canal.send("Carlos")
        canal.close()
    }

    for (nome in canal) {
        println("Recebido: $nome")
    }
}