package chess
fun fancyPrintln(input: Any){
    println()
    println(input)
    println()
}

fun Int.toColumn(): String {
    return when (this) {
        in 1..8 -> ('a' + this - 1).toString()
        else -> throw IllegalArgumentException("Invalid column number: $this. Column number should be between 0 and 7.")
    }
}

fun String.toColumnNumber(): Int {
    return when (this) {
        in "a".."h" -> this[0] - 'a'
        else -> throw IllegalArgumentException("Invalid column: $this. Column should be between 'a' and 'h'.")
    }
}