package chess.mlmodels.dataframes

class DataProcessing {
    fun convertJsonToPieceCount(jsonString: String): PieceCount {
        val map = sanitizeJson<Int>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return PieceCount(
            map["blackrook"] ?: 0,
            map["blackknight"] ?: 0,
            map["blackbishop"] ?: 0,
            map["blackqueen"] ?: 0,
            map["blackking"] ?: 0,
            map["blackpawn"] ?: 0,
            map["empty"] ?: 0,
            map["whitepawn"] ?: 0,
            map["whiterook"] ?: 0,
            map["whiteknight"] ?: 0,
            map["whitebishop"] ?: 0,
            map["whitequeen"] ?: 0,
            map["whiteking"] ?: 0,
        )
    }

    fun convertJsonToPieceActivity(jsonString: String): PieceActivity {
        val map = sanitizeJson<Int>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return PieceActivity(
            map["blackrook"] ?: 0,
            map["blackknight"] ?: 0,
            map["blackbishop"] ?: 0,
            map["blackqueen"] ?: 0,
            map["blackking"] ?: 0,
            map["blackpawn"] ?: 0,
            map["empty"] ?: 0,
            map["whitepawn"] ?: 0,
            map["whiterook"] ?: 0,
            map["whiteknight"] ?: 0,
            map["whitebishop"] ?: 0,
            map["whitequeen"] ?: 0,
            map["whiteking"] ?: 0,
        )
    }

    fun convertJsonToKingSafety(jsonString: String): KingSafety {
        val map = sanitizeJson<Int>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return KingSafety(
            map["whiteking"] ?: 0,
            map["blackking"] ?: 0,
        )
    }

    fun convertJsonToLegalMoves(jsonString: String): LegalMoves {
        val map = sanitizeJson<String>(jsonString, "[", "]")

        // Convert the map to a PieceCount object
        return LegalMoves(
            map["white"]?.split(",")?.map { it.trim() } ?: emptyList(),
            map["black"]?.split(",")?.map { it.trim() } ?: emptyList(),
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
        val map = sanitizeJson<Int>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return MaterialBalance(
            map["white"] ?: 0,
            map["black"] ?: 0,
        )
    }

    fun convertJsonToCenterControl(jsonString: String): CenterControl {
        // Replace underscores with camelCase
        val map = sanitizeJson<Int>(jsonString, "{", "}")

        // Convert the map to a PieceCount object
        return CenterControl(
            map["white"] ?: 0,
            map["black"] ?: 0,
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
                            T::class == Int::class ->
                                keyValuePair[1].substringAfter(afterDelimiter)
                                    .substringBefore(beforeDelimiter).toInt() as T

                            else -> keyValuePair[1].substringAfter(afterDelimiter).substringBefore(beforeDelimiter) as T
                        }
                    key to value
                }
        }
        return emptyMap()
    }
}
