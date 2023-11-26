package chess.common.model.players

import chess.common.model.Piece

interface Player {
    val name: String
    var playerPoints: Int
    val ownPieces: Set<Piece>
    val wonPieces: Set<Piece>
    val lostPieces: Set<Piece>
    val selectedPiece: Piece?
    val destinationPiece: Piece?

    fun defaultPieces(): List<List<Piece>>

    fun setOwnPieces()

    fun updateOwnPieces(
        selectedPiece: Piece?,
        destinationPiece: Piece?,
    )

    fun setSelectedPiece(piece: Piece): Boolean

    fun setDestinationPiece(piece: Piece, otherPlayerPiecePositions: List<String>): Boolean

    fun setWonPieces()

    fun setLostPieces(destinationPiece: Piece?)

    fun updatePlayerPoints()

    fun ownPiecePositions(): List<String>
}
