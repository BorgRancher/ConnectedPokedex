package tech.borgranch.pokedex.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var pokemonInfo: MutableLiveData<PokemonDetail> = MutableLiveData()
    val pokemonDetail: LiveData<PokemonDetail> get() = pokemonInfo

    suspend fun getPokemonDetail(name: String) {
        val localData = getPokemonDetailAsync(name).await()
        pokemonInfo.postValue(localData)
    }

    private suspend fun getPokemonDetailAsync(name: String = ""): Deferred<PokemonDetail> = coroutineScope.async(coroutineDispatcher) {
        val localDetail = detailDao.getPokemonDetailByName(name)
        when (localDetail != null) {
            true -> {
                return@async localDetail
            }
            else -> {
                fetchRemotePokemon(name)
                return@async detailDao.getPokemonDetailByName(name)!!
            }
        }
    }

    private suspend fun fetchRemotePokemon(name: String) {
        val result =
            pokedexClient.query(PokemonQuery(name = Optional.presentIfNotNull(name)))
                .execute()
        when (result.hasErrors()) {
            true -> {
                throw Exception(result.errors.toString())
            }
            else -> {
                val pokemonDetail = result.data?.pokemon?.toPokemonDetail()
                pokemonDetail?.let {
                    detailDao.insert(it)
                }
            }
        }
    }
}
