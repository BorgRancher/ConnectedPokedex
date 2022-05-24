package tech.borgranch.pokedex.data

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.borgranch.pokedex.data.dao.DetailDao
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.dto.PokemonDetail
import tech.borgranch.pokedex.data.dto.PokemonItem

/**
 * Offline storage for PokeDex data.
 * @author Shaun McDonald
 */
@Database(entities = [PokemonItem::class, PokemonDetail::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPokemonItemDao(): ListDao
    abstract fun getPokemonDetailDao(): DetailDao
}
