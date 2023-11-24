package common.model

data class Position(
    val row: Int,
    val column: String
){
    override fun toString(): String {
        return "$row$column"
    }
}
