package chess.common.model.players

import chess.common.model.Piece

interface Player {
    val name: String
    var points: Long
    val ownPieces: Set<Piece>
    val wonPieces: Set<Piece>

    fun defaultPieces(): List<List<Piece>>

    fun setOwnPieces()

    fun updateOwnPieces(
        selectedPiece: Piece,
        destinationPiece: Piece,
    )

    fun piecePositions(): List<String>
}
