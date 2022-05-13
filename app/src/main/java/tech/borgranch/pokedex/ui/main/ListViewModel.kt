package tech.borgranch.pokedex.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.data.repositories.ListRepository
import tech.borgranch.pokedex.di.DispatcherModule
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    listRepository: ListRepository
) : ViewModel() {
    val pokemonList: LiveData<List<PokemonItem>> = listRepository.allPokemon
    private var pokemonIndex = 1
    init {
        viewModelScope.launch(DispatcherModule.provideCoroutineDispatcher()) {
            listRepository.fetchPokeDex(pokemonIndex)
        }
    }
}
