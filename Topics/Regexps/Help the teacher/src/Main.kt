fun main() {
    val report = readLine()!!
    val regex = Regex(". wrong answers?")
    print(regex.matches(report))
}