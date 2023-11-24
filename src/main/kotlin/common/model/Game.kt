package common.model

import common.model.players.Player
import fancyPrintln

class Game(val pickedPlayer: Player, val otherPlayer: Player, val board: Board) {
    fun start(){
        val boardView = board.buildBoard(pickedPlayer)
        playerAction()
    }
    fun playerAction(){
        do {
            fancyPrintln("Please enter your first move (example: e2-e4): ")
            val move = readlnOrNull()
            val correctInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
            if(correctInput){
                playMove()
            } else {
                fancyPrintln("Please enter a valid input like (e2-e4)")
            }
        } while (!correctInput)
    }
    fun playMove(){
        TODO()
    }
}