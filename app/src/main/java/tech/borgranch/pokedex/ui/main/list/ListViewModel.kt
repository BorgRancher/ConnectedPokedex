package tech.borgranch.pokedex.ui.main.list

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.data.repositories.ListRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val isLoading: LiveData<Boolean> = listRepository.loading
    val errorMessage: LiveData<String> = listRepository.errorMessage
    val supervisorJob = SupervisorJob()
    val fetchJob = Job(supervisorJob)
    val coroutineContext = fetchJob + ioDispatcher
    val pokemonList: LiveData<List<PokemonItem>> = listRepository.allPokemon
    private var pokemonIndex = MutableLiveData<Int>(0)
    init {
        Timber.d("ListViewModel created")
        viewModelScope.launch {

            // pokemonIndex.observeForever { page ->
            //     Timber.d("ListViewModel: fetching page $page")
            //     listRepository.fetchPokeDex(page)
            // }

            Transformations.map(isLoading) { it ->
                if (!it) {
                    pokemonIndex.value = pokemonIndex.value?.plus(1) ?: 0
                }
            }
        }
    }

    @MainThread
    fun fetchNextPokemonList() = viewModelScope.launch {
        if (!isLoading.value!!) {
            pokemonIndex.value = pokemonIndex.value?.plus(1)
            withContext(ioDispatcher) {
                listRepository.fetchPokeDex(pokemonIndex.value!!)
            }
        }
    }
}
