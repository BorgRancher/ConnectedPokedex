package tech.borgranch.pokedex.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.borgranch.pokedex.data.AppDatabase
import tech.borgranch.pokedex.data.dao.DetailDao
import tech.borgranch.pokedex.data.dao.ListDao
import javax.inject.Singleton

/**
 * @author Shaun McDonald
 * @version 1.2
 * Offline storage for Pokedex implemented in Android Room
 */

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
    ): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "Pokedex.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDetailDao(appDatabase: AppDatabase): DetailDao {
        return appDatabase.getPokemonDetailDao()
    }

    @Provides
    @Singleton
    fun provideListDao(appDatabase: AppDatabase): ListDao {
        return appDatabase.getPokemonItemDao()
    }
}
