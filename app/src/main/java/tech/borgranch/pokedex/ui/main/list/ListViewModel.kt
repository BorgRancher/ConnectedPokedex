package tech.borgranch.pokedex.ui.main.list

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.data.repositories.ListRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class ListViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    var animShown: Boolean = false
    val limit = ListRepository.POKEMON_LIMIT
    val errorMessage: LiveData<String> = listRepository.errorMessage
    private val supervisorJob = SupervisorJob()
    private val fetchJob = Job(supervisorJob)
    val pokemonList: LiveData<List<PokemonItem>> = listRepository.allPokemon
    private var pokemonIndex: MutableLiveData<Int> = MutableLiveData(-1)
    private var coroutineContext: CoroutineContext
    val pokemonPage: LiveData<Int> get() = pokemonIndex
    init {
        Timber.d("ListViewModel created")
        coroutineContext = fetchJob + this.ioDispatcher
        pokemonIndex.value = -1
    }

    @MainThread
    fun fetchNextPokemonList() {
        viewModelScope.launch {
            if (!isLoading() && !isLastPage()) {
                pokemonIndex.value = pokemonIndex.value?.plus(1)
                withContext(coroutineContext) {
                    listRepository.fetchPokeDex(pokemonPage.value!!)
                }
            }
        }
    }

    fun isLoading(): Boolean = listRepository.loading.value ?: false
    fun isLastPage(): Boolean = listRepository.listEnded.value ?: false

    @MainThread
    fun retryPokemonList() {
        viewModelScope.launch {
            if (!isLoading() && !isLastPage()) {
                withContext(coroutineContext) {
                    listRepository.fetchPokeDex(pokemonIndex.value ?: 1)
                }
            }
        }
    }

    override fun onCleared() {
        supervisorJob.cancelChildren()
    }
}
