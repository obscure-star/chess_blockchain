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
    var isChecked: Boolean

    fun defaultPieces(): List<List<Piece>>

    fun setOwnPieces()

    fun updateOwnPieces(
        selectedPiece: Piece?,
        destinationPiece: Piece?,
    )

    fun updateAllOpenMoves(otherPlayerPiecePositions: List<String>)

    fun setSelectedPiece(piece: Piece): Boolean

    fun setDestinationPiece(
        piece: Piece,
        otherPlayerPiecePositions: List<String>,
    ): Boolean

    fun setWonPieces()

    fun setLostPieces(destinationPiece: Piece?)

    fun updatePlayerPoints()

    fun ownPiecePositions(): List<String>

    fun setCheck()

    fun unCheck()
}
