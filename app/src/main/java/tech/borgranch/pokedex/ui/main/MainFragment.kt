package tech.borgranch.pokedex.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.viewbinding.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.databinding.MainFragmentBinding
import tech.borgranch.pokedex.databinding.PokemonItemBinding
import tech.borgranch.pokedex.ui.main.list.PokemonCard

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<ListViewModel>()
    private var _ui: MainFragmentBinding? = null
    private val ui: MainFragmentBinding get() = _ui!!
    private val groupAdaptor = GroupAdapter<GroupieViewHolder<PokemonItemBinding>>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = MainFragmentBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        lifecycleScope.launchWhenResumed {
            viewModel.pokemonList.observe(viewLifecycleOwner) { pokemonList ->
                // Update the cached copy of the pokemonList in the adapter.
                pokemonList?.let {
                    initRecyclerView(it.toPokemonCards())
                }
            }
        }
    }

    private fun initRecyclerView(pokemonCards: List<PokemonCard>) {
        groupAdaptor.apply {
            addAll(pokemonCards)
            notifyItemRangeInserted(0, pokemonCards.size)
        }
        ui.pokemonsList.apply {
            layoutManager = GridLayoutManager(this@MainFragment.requireContext(), 2)
            adapter = groupAdaptor
        }
    }

    private fun List<PokemonItem>.toPokemonCards(): List<PokemonCard> {
        return this.map { pokemonItem -> PokemonCard(pokemonItem) }
    }
}
