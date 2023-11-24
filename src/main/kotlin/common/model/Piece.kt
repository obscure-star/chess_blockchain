package common.model

import common.model.pieceTypes.PieceType

data class Piece(
    val name: String?,
    val pieceType: PieceType?,
    val position: Position,
    val openMoves: List<Position> = emptyList(),
    val restrictedMoves: List<Position> = emptyList()
    )
