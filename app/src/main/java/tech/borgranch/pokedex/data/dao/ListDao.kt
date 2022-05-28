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
    fun insertAll(pokemonItems: List<PokemonItem>)

    @Query("SELECT * FROM pokemon_item WHERE page = :page")
    fun getPage(page: Int): List<PokemonItem>

    @Query("SELECT * FROM pokemon_item WHERE page <= :page")
    fun getCurrentPages(page: Int): List<PokemonItem>

    @Query("SELECT * FROM pokemon_item WHERE name like :name")
    fun getPokemonByName(name: String): List<PokemonItem>

    @Query("SELECT EXISTS(SELECT 1 FROM pokemon_item WHERE name = :name)")
    fun exists(name: String?): Boolean

    @Query("UPDATE pokemon_item SET artwork= :filePath WHERE name = :name")
    fun updateImage(name: String, filePath: String)
}
