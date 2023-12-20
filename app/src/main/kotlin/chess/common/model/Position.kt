package chess.common.model

data class Position(
    val row: Int,
    val column: String,
    var pawnFowardMove: Boolean = false,
) {
    override fun toString(): String {
        if (pawnFowardMove) {
            return "$column${row}p"
        }
        return "$column$row"
    }

    fun toStringWithoutP(): String {
        return "$column$row"
    }
}
