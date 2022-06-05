package tech.borgranch.pokedex.data.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tech.borgranch.pokedex.data.converters.DataMappers.toPokemonItem
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.errors.PokeDexErrorHandler
import tech.borgranch.pokedex.graphql.PokemonsQuery
import timber.log.Timber
import javax.inject.Inject

class ListRepository @Inject constructor(
    private val itemDao: ListDao,
    private val pokeApiClient: ApolloClient,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    companion object {
        const val POKEMON_LIMIT = 20
    }

    private val loadingState: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = loadingState

    private val errorState: MutableLiveData<String> = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = errorState

    private var coroutineScope = CoroutineScope(coroutineDispatcher)
    private var pokemonList: MutableLiveData<List<PokemonItem>> = MutableLiveData()
    val allPokemon: LiveData<List<PokemonItem>> get() = pokemonList

    @WorkerThread
    suspend fun fetchPokeDex(page: Int) {
        coroutineScope.launch(coroutineDispatcher) {
            fetchPokeDexAsync(page)
        }
    }

    fun getPagedPokemon(page: Int = 1): Flow<PagingData<PokemonItem>> {
        Timber.d("New network page: $page")
        return Pager(
            config = PagingConfig(pageSize = POKEMON_LIMIT, enablePlaceholders = true),
            pagingSourceFactory = { ListPagingSource(pokeApiClient, itemDao) }
        ).flow
    }

    @WorkerThread
    private suspend fun fetchPokeDexAsync(page: Int) {
        loadingState.postValue(true)
        val pokemons: List<PokemonItem> = itemDao.getPage(page)
        if (pokemons.isNotEmpty()) {
            // already in db
            val data = itemDao.getCurrentPages(page)
            pokemonList.postValue(data)
            loadingState.postValue(false)
        } else {
            fetchRemotePokemon(page).also {
                val data = itemDao.getCurrentPages(page)
                pokemonList.postValue(data)
                loadingState.postValue(false)
            }
        }
    }

    @WorkerThread
    private suspend fun fetchRemotePokemon(page: Int) {
        try {
            val query = PokemonsQuery(
                limit = Optional.presentIfNotNull(POKEMON_LIMIT),
                offset = Optional.presentIfNotNull(POKEMON_LIMIT * page)
            )
            val incoming = pokeApiClient.query(query).execute()
            if (!incoming.hasErrors()) {

                incoming.data?.pokemons?.results?.mapNotNull { result ->
                    result?.let {
                        if (!itemDao.exists(it.name)) {
                            // Push pokemon to db
                            itemDao.insert(it.toPokemonItem(page))
                        }
                    }
                }
            } else {
                for (error in incoming.errors!!) {
                    errorState.postValue(error.message)
                }
            }
        } catch (e: Exception) {
            val humanReadableError = PokeDexErrorHandler.getHumanReadableErrorMessage(e)
            errorState.postValue(humanReadableError)
        } finally {
            loadingState.postValue(false)
        }
    }
}
