package chess.mlmodels.dataframes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegalMoves(
    @SerialName("white")
    val white: List<String>,
    @SerialName("black")
    val black: List<String>,
) {
    fun getLegalMoves(playerName: String): List<String> {
        return if (playerName == "white") {
            white
        } else {
            black
        }
    }
}
