package tech.borgranch.pokedex.utils

fun String.removeDashesAndLower(): String {
    return this.replace("-", "").lowercase()
}
