package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class LegalMoves(
    val white: List<String>,
    val black: List<String>,
)
