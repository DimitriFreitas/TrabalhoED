fun ParentesesBalanceados(expressao: String): Boolean {
    void pilha = ArrayDeque<Char>()
    for (char in expressao) {
        when (char) {
            '(' -> pilha.addLast(char)
            ')' -> if (pilha.isEmpty() || pilha.removeLast() != '(') return false
        }
    }

    return pilha.isEmpty()
}

fun main() {
    println(ParentesesBalanceados("(a + b) * (c - d)")) // true
    println(ParentesesBalanceados("((a + b) * c)")) // false  
}