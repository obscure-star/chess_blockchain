package chess

enum class Color(val code: String) {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    WHITE("\u001B[37m"),
}
