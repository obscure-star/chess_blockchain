package chess.common.model.players

import chess.common.model.Piece
import chess.common.model.Position

interface Player {
    val name: String
    val playerPoints: Int
    val ownPieces: Set<Piece>
    val wonPieces: Set<Piece>
    val lostPieces: Set<Piece>
    val selectedPiece: Piece?
    val destinationPiece: Piece?
    val allOpenMoves: List<Position>
    val previousState: Player?
    val winner: Boolean

    fun saveState()

    fun restoreState()

    fun defaultPieces(): List<List<Piece>>

    fun setOwnPieces()

    fun updateOwnPieces(
        selectedPiece: Piece?,
        destinationPiece: Piece?,
    )

    fun updateAllOpenMoves(
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    )

    fun getInstanceAllOpenMoves(
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ): List<Position>

    fun setSelectedPiece(piece: Piece): Boolean

    fun setDestinationPiece(piece: Piece): Boolean

    fun setWonPieces()

    fun setLostPieces(destinationPiece: Piece?)

    fun updatePlayerPoints()

    fun getOwnPiecePositions(): List<String>

    fun setWinner()
}
