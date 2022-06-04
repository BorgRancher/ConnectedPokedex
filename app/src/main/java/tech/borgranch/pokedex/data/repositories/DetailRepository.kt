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
    private var pokemonNames: MutableLiveData<List<String>> = MutableLiveData()
    val pokemonNamesList: LiveData<List<String>> get() = pokemonNames

    suspend fun getPokemonDetail(name: String) {
        val localData = getPokemonDetailAsync(name).await()
        pokemonInfo.postValue(localData)
    }

    suspend fun getPokemonNamesForType(type: String) {
        val localData = getPokemonNamesForTypeAsync(type).await()
        pokemonNames.postValue(localData)
    }

    private suspend fun getPokemonDetailAsync(name: String): Deferred<PokemonDetail> {
        return coroutineScope.async(coroutineDispatcher) {
            val pokemon = detailDao.getPokemonDetailByName(name)
            if (pokemon != null) {
                pokemon
            } else {
                val pokemonDetail = fetchRemotePokemon(name)
                detailDao.insert(pokemonDetail!!)
                pokemonDetail
            }
        }
    }

    private fun getPokemonNamesForTypeAsync(type: String): Deferred<List<String>> {
        return coroutineScope.async(coroutineDispatcher) {
            val pokemonNames = detailDao.getPokemonNamesByType(type)
            pokemonNames.ifEmpty {
                emptyList()
            }
        }
    }

    private suspend fun fetchRemotePokemon(name: String): PokemonDetail? {
        val pokemonQuery = PokemonQuery(Optional.presentIfNotNull(name))
        val pokemonDetail = pokedexClient.query(pokemonQuery).execute().data?.pokemon
        return pokemonDetail?.toPokemonDetail()
    }
}
