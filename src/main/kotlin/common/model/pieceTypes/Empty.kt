package common.model.pieceTypes

data class Empty(
    override val name: String = "empty",
    override val image: String = " _ "
    ): PieceType
