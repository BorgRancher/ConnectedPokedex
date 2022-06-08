package tech.borgranch.pokedex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.borgranch.pokedex.data.dao.DetailDao
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.dto.PokemonDetail
import tech.borgranch.pokedex.data.dto.PokemonItem

/**
 * Offline storage for PokeDex data.
 * @author Shaun McDonald
 * @since v0.0.1
 */
@Database(entities = [PokemonItem::class, PokemonDetail::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPokemonItemDao(): ListDao
    abstract fun getPokemonDetailDao(): DetailDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        const val DATABASE_NAME = "pokedex.db"

        @JvmStatic
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            return INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        @JvmStatic
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
