package common.model

import common.model.pieceTypes.PieceType

data class Piece(
    val name: String?,
    val pieceType: PieceType?,
    var position: Position = Position(1, "a"),
    var openMoves: Set<Position> = emptySet(),
    var restrictedMoves: Set<Position> = emptySet()
    ){
    fun setOpenMoves(playerPiecePosition: List<String>){
        openMoves = pieceType?.movePattern(position, playerPiecePositions = playerPiecePosition) ?: emptySet()
    }
}
