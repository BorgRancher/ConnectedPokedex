package tech.borgranch.pokedex.data.repositories

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import tech.borgranch.pokedex.PokedexApp
import tech.borgranch.pokedex.data.converters.DataMappers.toPokemonItem
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.errors.PokeDexErrorHandler
import tech.borgranch.pokedex.graphics.BitmapUtils
import tech.borgranch.pokedex.graphql.PokemonsQuery
import javax.inject.Inject

class ListRepository @Inject constructor(
    private val itemDao: ListDao,
    private val pokeApiClient: ApolloClient,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    companion object {
        const val POKEMON_LIMIT = 8
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
        pokemonList.postValue(fetchPokeDexAsync(page).await())
    }

    @WorkerThread
    private suspend fun fetchPokeDexAsync(page: Int): Deferred<List<PokemonItem>> = coroutineScope.async {
        loadingState.postValue(true)
        val pokemons = itemDao.getPage(page)
        if (pokemons.isNotEmpty()) {
            // already in db
            val data = itemDao.getCurrentPages(page)
            loadingState.postValue(false)
            return@async data
        }
        return@async pokemons.ifEmpty {
            fetchRemotePokemon(page)
            itemDao.getCurrentPages(page)
        }
    }

    @WorkerThread
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
                        if (!itemDao.exists(it.name)) {
                            // Save image to local file system
                            saveArtwork(it.toPokemonItem(page))
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

    private fun saveArtwork(it: PokemonItem) = coroutineScope.launch(coroutineDispatcher) {
        Glide.with(PokedexApp.instance)
            .asBitmap()
            .load(it.artwork)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    errorState.postValue("Failed to load image")
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    coroutineScope.launch(coroutineDispatcher) {
                        val filePath =
                            BitmapUtils.saveBitmap(resource, PokedexApp.instance)
                        val updatedPokemon = it.copy(artwork = filePath)
                        itemDao.insert(updatedPokemon)
                    }
                }
            })
    }
}
