package tech.borgranch.pokedex.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tech.borgranch.pokedex.data.dto.PokemonDetail

@Dao
interface DetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pokemonDetail: PokemonDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg pokemonDetails: PokemonDetail)

    @Query("SELECT * FROM pokemon_detail WHERE name = :name")
    fun getPokemonDetailByName(name: String): PokemonDetail?

    @Query("SELECT name FROM pokemon_detail WHERE types LIKE :type")
    fun getPokemonNamesByType(type: String): List<String>
}
