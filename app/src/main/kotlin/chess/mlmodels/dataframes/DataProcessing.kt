package chess.mlmodels.dataframes

class DataProcessing {
    fun convertJsonToPieceCount(jsonString: String): PieceCount {
        val map = sanitizeJson<Double>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return PieceCount(
            blackRook = map["blackrook"] ?: 0.0,
            blackKnight = map["blackknight"] ?: 0.0,
            blackBishop = map["blackbishop"] ?: 0.0,
            blackQueen = map["blackqueen"] ?: 0.0,
            blackKing = map["blackking"] ?: 0.0,
            blackPawn = map["blackpawn"] ?: 0.0,
            empty = map["empty"] ?: 0.0,
            whitePawn = map["whitepawn"] ?: 0.0,
            whiteRook = map["whiterook"] ?: 0.0,
            whiteKnight = map["whiteknight"] ?: 0.0,
            whiteBishop = map["whitebishop"] ?: 0.0,
            whiteQueen = map["whitequeen"] ?: 0.0,
            whiteKing = map["whiteking"] ?: 0.0,
        )
    }

    fun convertJsonToPieceActivity(jsonString: String): PieceActivity {
        val map = sanitizeJson<Double>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return PieceActivity(
            blackRook = map["blackrook"] ?: 0.0,
            blackKnight = map["blackknight"] ?: 0.0,
            blackBishop = map["blackbishop"] ?: 0.0,
            blackQueen = map["blackqueen"] ?: 0.0,
            blackKing = map["blackking"] ?: 0.0,
            blackPawn = map["blackpawn"] ?: 0.0,
            empty = map["empty"] ?: 0.0,
            whitePawn = map["whitepawn"] ?: 0.0,
            whiteRook = map["whiterook"] ?: 0.0,
            whiteKnight = map["whiteknight"] ?: 0.0,
            whiteBishop = map["whitebishop"] ?: 0.0,
            whiteQueen = map["whitequeen"] ?: 0.0,
            whiteKing = map["whiteking"] ?: 0.0,
        )
    }

    fun convertJsonToKingSafety(jsonString: String): KingSafety {
        val map = sanitizeJson<Double>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return KingSafety(
            whiteKing = map["whiteking"] ?: 0.0,
            blackKing = map["blackking"] ?: 0.0,
        )
    }

    fun convertJsonToLegalMoves(jsonString: String): LegalMoves {
        val map = sanitizeJson<String>(jsonString, "[", "]")

        // Convert the map to a PieceCount object
        return LegalMoves(
            white = map["white"]?.split(",")?.map { it.trim() } ?: emptyList(),
            black = map["black"]?.split(",")?.map { it.trim() } ?: emptyList(),
        )
    }

    fun convertJsonToThreatsAndAttacks(jsonString: String): ThreatsAndAttacks {
        // Replace underscores with camelCase
        val map = sanitizeJson<String>(jsonString, "[", "]")

        // Convert the map to a PieceCount object
        return ThreatsAndAttacks(
            map["white"]?.split(",")?.map { it.trim() } ?: emptyList(),
            map["black"]?.split(",")?.map { it.trim() } ?: emptyList(),
        )
    }

    fun convertJsonToMaterialBalance(jsonString: String): MaterialBalance {
        // Replace underscores with camelCase
        val map = sanitizeJson<Double>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return MaterialBalance(
            white = map["white"] ?: 0.0,
            black = map["black"] ?: 0.0,
        )
    }

    fun convertJsonToCenterControl(jsonString: String): CenterControl {
        // Replace underscores with camelCase
        val map = sanitizeJson<Double>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return CenterControl(
            white = map["white"] ?: 0.0,
            black = map["black"] ?: 0.0,
        )
    }

    private inline fun <reified T> sanitizeJson(
        jsonString: String,
        afterDelimiter: String,
        beforeDelimiter: String,
    ): Map<String, T> {
        val camelCaseJson = jsonString.replace("_", "")

        // Remove curly braces
        if (camelCaseJson.length > 4) {
            val cleanedJson = camelCaseJson.substring(2, camelCaseJson.length - 2)

            // Split into key-value pairs
            val keyValuePairs = cleanedJson.split(", ")

            // Create a map from key-value pairs
            return keyValuePairs
                .map { it.split("=") }
                .associate { keyValuePair ->
                    val key = keyValuePair[0]
                    val value =
                        when {
                            // Check if T is Int and convert the value accordingly
                            T::class == Double::class ->
                                keyValuePair[1].substringAfter(afterDelimiter)
                                    .substringBefore(beforeDelimiter).toDouble() as T

                            else -> keyValuePair[1].substringAfter(afterDelimiter).substringBefore(beforeDelimiter) as T
                        }
                    key to value
                }
        }
        return emptyMap()
    }
}
