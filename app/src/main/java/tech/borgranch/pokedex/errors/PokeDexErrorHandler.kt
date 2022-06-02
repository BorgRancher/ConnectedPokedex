package tech.borgranch.pokedex.errors

import java.io.IOException

object PokeDexErrorHandler {
    fun getHumanReadableErrorMessage(e: Throwable): String {
        return when (e) {
            is IOException -> "There was an error fetching Pokemon data from the network. Swipe down to retry."
            else -> e.message ?: "An unknown error occurred."
        }
    }
}
