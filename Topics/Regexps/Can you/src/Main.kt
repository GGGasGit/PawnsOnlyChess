fun main() {
    val answer = readln()
    val regex = Regex("I can'?t? do my homework on time!")
    print(regex.matches(answer))
}