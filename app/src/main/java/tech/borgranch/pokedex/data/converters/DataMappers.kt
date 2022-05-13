package tech.borgranch.pokedex.data.converters

import tech.borgranch.pokedex.data.dto.PokemonDetail
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.graphql.PokemonQuery
import tech.borgranch.pokedex.graphql.PokemonsQuery

object
DataMappers {
    fun PokemonQuery.Pokemon?.toPokemonDetail(): PokemonDetail {
        return PokemonDetail(
            name = this?.name!!,
            height = this.height ?: 0,
            weight = this.weight ?: 0,
            species = this.species?.name ?: "",
            types = this.types?.mapNotNull { it -> it?.type?.name }?.joinToString(", ").orEmpty(),
            sprites = this.sprites?.get(0)?.front_default ?: "",
            abilities = this.abilities?.mapNotNull { it?.ability?.name }?.joinToString(", ").orEmpty(),
        )
    }

    fun PokemonsQuery.Result.toPokemonItem(page: Int): PokemonItem {
        return PokemonItem(
            name = this.name!!,
            url = this.url!!,
            artwork = this.artwork!!,
            image = this.image!!,
            dreamworld = this.dreamworld!!,
            page = page
        )
    }
}
