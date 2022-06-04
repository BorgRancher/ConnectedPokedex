package tech.borgranch.pokedex.data.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.load.HttpException
import tech.borgranch.pokedex.data.dao.ListDao
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.graphql.PokemonsQuery
import java.io.IOException

class ListPagingSource(
    private val pokeDexClient: ApolloClient,
    private val itemDao: ListDao
) : PagingSource<Int, PokemonItem>() {

    companion object {
        const val POKEDEX_STARTING_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonItem>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    /**
     * Loading API for [PagingSource].
     *
     * Implement this method to trigger your async load (e.g. from database or network).
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonItem> {
        val page = params.key ?: POKEDEX_STARTING_PAGE
        val query = PokemonsQuery(
            limit = Optional.presentIfNotNull(params.loadSize),
            offset = Optional.presentIfNotNull(page * params.loadSize)
        )

        try {
            val response = pokeDexClient.query(query).execute()
            val items = response.data?.pokemons?.results
            val data = items?.mapNotNull {
                PokemonItem(
                    name = it?.name!!,
                    url = it.url!!,
                    artwork = it.artwork!!,
                    image = it.image!!,
                    dreamworld = it.dreamworld!!,
                    page = page
                )
            }.orEmpty().toList()
            val nextKey = if (data.isEmpty()) null else page + 1
            return if (data.isEmpty()) {
                LoadResult.Error(IOException("No data"))
            } else {
                itemDao.insertAll(data)
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == POKEDEX_STARTING_PAGE) null else page - 1,
                    nextKey = nextKey
                )
            }
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}
