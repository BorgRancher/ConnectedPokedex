package tech.borgranch.pokedex.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tech.borgranch.pokedex.data.dto.PokemonItem

@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pokemonItem: PokemonItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg pokemonItems: PokemonItem)

    @Query("SELECT * FROM pokemon_item WHERE page = :page")
    fun getPage(page: Int): List<PokemonItem>

    @Query("SELECT * FROM pokemon_item WHERE page <= :page")
    fun getCurrentPages(page: Int): List<PokemonItem>

    @Query("SELECT * FROM pokemon_item WHERE name like :name")
    fun getPokemonByName(name: String): List<PokemonItem>
}
