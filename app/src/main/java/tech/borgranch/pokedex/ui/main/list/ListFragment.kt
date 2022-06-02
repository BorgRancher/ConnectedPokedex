package tech.borgranch.pokedex.ui.main.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.viewbinding.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.databinding.FragmentListBinding
import tech.borgranch.pokedex.databinding.ItemPokemonBinding
import java.net.URI

@AndroidEntryPoint
class ListFragment : Fragment() {

    companion object {
        fun newInstance() = ListFragment()
    }

    private var selectedIndex: Int = -1

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: URI)
    }

    private val viewModel by activityViewModels<ListViewModel>()
    private var pokemonOffset: Int = 0
    private var _ui: FragmentListBinding? = null
    private val ui: FragmentListBinding get() = _ui!!
    private val groupAdaptor = GroupAdapter<GroupieViewHolder<ItemPokemonBinding>>()
    private val navArgs by navArgs<ListFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = FragmentListBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        lifecycleScope.launchWhenResumed {
            navArgs.let {
                selectedIndex = it.selectedIndex
            }
            if (selectedIndex == -1) {
                viewModel.fetchNextPokemonList()
            } else {
                viewModel.retryPokemonList()
            }
            viewModel.pokemonPage.observe(viewLifecycleOwner) {
                pokemonOffset = it * viewModel.limit
            }
            viewModel.pokemonList.observe(viewLifecycleOwner) { pokemonList ->
                // Update the cached copy of the pokemonList in the adapter.
                pokemonList?.let {
                    initRecyclerView(it.toPokemonCards())
                }
            }

            ui.progressBar.visibility = if (viewModel.isLoading()) View.VISIBLE else View.GONE
        }
    }

    private fun initRecyclerView(pokemonCards: List<PokemonListCard>) {
        groupAdaptor.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            update(pokemonCards)
        }

        ui.pokemonsList.apply {
            layoutManager = GridLayoutManager(this@ListFragment.requireContext(), 2)
            adapter = groupAdaptor
        }

        if (selectedIndex != -1) {
            val lm = ui.pokemonsList.layoutManager as GridLayoutManager
            lm.scrollToPositionWithOffset(selectedIndex, 20)
        }

        val recyclerViewPager = RecyclerViewPager(
            recyclerView = ui.pokemonsList,
            isLoading = { viewModel.isLoading() },
            loadMore = { viewModel.fetchNextPokemonList() },
            onLast = { false }
        )
        recyclerViewPager.resetCurrentPage()
    }

    private fun List<PokemonItem>.toPokemonCards(): List<PokemonListCard> {
        return this.map { pokemonItem -> PokemonListCard(pokemonItem) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }
}
