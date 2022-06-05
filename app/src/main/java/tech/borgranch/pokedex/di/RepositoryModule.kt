package tech.borgranch.pokedex.di

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import tech.borgranch.pokedex.data.dao.DetailDao
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.repositories.DetailRepository
import tech.borgranch.pokedex.data.repositories.ListRepository

/**
 * @author Shaun McDonald
 * @since 2022-06-01
 * @version 1.0.0
 * Module providing dependencies for the ViewModels
 */

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideListRepository(
        pokedexClient: ApolloClient,
        listDao: ListDao,
        coroutineDispatcher: CoroutineDispatcher
    ): ListRepository {
        return ListRepository(
            itemDao = listDao,
            pokeApiClient = pokedexClient,
            coroutineDispatcher = coroutineDispatcher
        )
    }

    @Provides
    @ViewModelScoped
    fun provideDetailRepository(
        pokedexClient: ApolloClient,
        detailDao: DetailDao,
        coroutineDispatcher: CoroutineDispatcher
    ): DetailRepository {
        return DetailRepository(
            detailDao = detailDao,
            pokedexClient = pokedexClient,
            coroutineDispatcher = coroutineDispatcher
        )
    }
}
