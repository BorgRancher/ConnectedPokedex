package tech.borgranch.pokedex.ui.main.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.borgranch.pokedex.data.dto.PokemonDetail
import tech.borgranch.pokedex.data.repositories.DetailRepository
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DetailRepository
) : ViewModel() {
    val pokemonDetail: LiveData<PokemonDetail> = repository.pokemonDetail
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    fun getPokemonDetail(name: String) {
        viewModelScope.launch {
            try {
                repository.getPokemonDetail(name)
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }
}
