package common.model.players

import common.model.Piece

interface Player {
    val name: String
    var points: Long
    val ownPieces: List<Piece>
    val wonPieces: List<Piece>
    fun defaultPieces(): List<List<Piece>>
}