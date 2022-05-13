package tech.borgranch.pokedex.data.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import tech.borgranch.pokedex.data.converters.DataMappers.toPokemonItem
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.graphql.PokemonsQuery
import javax.inject.Inject

class ListRepository @Inject constructor(
    private val itemDao: ListDao,
    private val pokeApiClient: ApolloClient,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    companion object {
        const val POKEMON_LIMIT = 20
    }

    private var coroutineScope = CoroutineScope(coroutineDispatcher)
    private var pokemonList: MutableLiveData<List<PokemonItem>> = MutableLiveData()
    val allPokemon: LiveData<List<PokemonItem>> get() = pokemonList

    fun fetchPokeDex(page: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            pokemonList.postValue(fetchPokeDexAsync(page).await())
        }
    }

    @WorkerThread
    private suspend fun fetchPokeDexAsync(page: Int): Deferred<List<PokemonItem>> = coroutineScope.async {
        val pokemons = itemDao.getPage(page)
        return@async pokemons.ifEmpty {
            fetchRemotePokemon(page)
            itemDao.getPage(page)
        }
    }

    private suspend fun fetchRemotePokemon(page: Int) {
        try {
            val incoming = pokeApiClient.query(
                PokemonsQuery(
                    limit = Optional.presentIfNotNull(POKEMON_LIMIT),
                    offset = Optional.presentIfNotNull(POKEMON_LIMIT * page)
                )
            ).execute()
            if (!incoming.hasErrors()) {

                incoming.data?.pokemons?.results?.mapNotNull { result ->
                    result?.let {
                        itemDao.insert(it.toPokemonItem(page))
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}
