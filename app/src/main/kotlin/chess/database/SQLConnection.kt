package chess.database

import chess.fancyPrintln
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object SQLConnection {
    @JvmStatic
    suspend fun connection(): Connection? =
        withContext(Dispatchers.IO) {
            try {
                val url = "jdbc:mysql://localhost:3306/Chess_database"
                val user = "root"
                val password = "obscure-star1234"

                fancyPrintln("Connected to SQL database!")
                DriverManager.getConnection(
                    url,
                    user,
                    password,
                )
            } catch (e: SQLException) {
                e.printStackTrace()
                return@withContext null
            }
        }
}
