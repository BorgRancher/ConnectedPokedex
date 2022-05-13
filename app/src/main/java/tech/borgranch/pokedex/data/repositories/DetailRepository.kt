package tech.borgranch.pokedex.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import tech.borgranch.pokedex.data.converters.DataMappers.toPokemonDetail
import tech.borgranch.pokedex.data.dao.DetailDao
import tech.borgranch.pokedex.data.dto.PokemonDetail
import tech.borgranch.pokedex.graphql.PokemonQuery
import javax.inject.Inject

class DetailRepository @Inject constructor(
    val detailDao: DetailDao,
    val pokedexClient: ApolloClient,
    val coroutineDispatcher: CoroutineDispatcher
) {
    private var coroutineScope = CoroutineScope(coroutineDispatcher)
    private var pokemonInfo: MutableLiveData<PokemonDetail> = MutableLiveData()
    val pokemonDetail: LiveData<PokemonDetail> get() = pokemonInfo

    fun getPokemonDetail(name: String) {
        coroutineScope.launch {
            val deferred = getPokemonDetailAsync(name).await()
            deferred?.let {
                pokemonInfo.postValue(it)
            }
        }
    }

    private fun getPokemonDetailAsync(name: String = ""): Deferred<PokemonDetail?> = coroutineScope.async {
        val localDetail = detailDao.getPokemonDetailByName(name)
        when (localDetail != null) {
            true -> {
                return@async localDetail
            }
            else -> {
                return@async fetchRemotePokemon(name)
            }
        }
    }

    private suspend fun fetchRemotePokemon(name: String): PokemonDetail? {
        val result =
            pokedexClient.query(PokemonQuery(name = Optional.presentIfNotNull(name)))
                .execute()
        return when (result.hasErrors()) {
            true -> {
                throw Exception(result.errors.toString())
            }
            else -> {
                val pokemonDetail = result.data?.pokemon?.toPokemonDetail()
                pokemonDetail?.let {
                    detailDao.insert(it)
                    return@let it
                }
            }
        }
    }
}
