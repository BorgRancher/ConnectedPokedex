package tech.borgranch.pokedex.ui.main.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

    private val selectedIndex: Int = -1

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: URI)
    }

    private val viewModel by viewModels<ListViewModel>()
    private var _ui: FragmentListBinding? = null
    private val ui: FragmentListBinding get() = _ui!!
    private val groupAdaptor = GroupAdapter<GroupieViewHolder<ItemPokemonBinding>>()
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
            if (selectedIndex == -1) {
                viewModel.fetchNextPokemonList()
            }
            viewModel.pokemonList.observe(viewLifecycleOwner) { pokemonList ->
                // Update the cached copy of the pokemonList in the adapter.
                pokemonList?.let {
                    initRecyclerView(it.toPokemonCards())
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
                ui.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initRecyclerView(pokemonCards: List<PokemonListCard>) {
        groupAdaptor.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            addAll(pokemonCards)
            notifyItemRangeChanged(0, pokemonCards.size)
        }
        ui.pokemonsList.apply {
            layoutManager = GridLayoutManager(this@ListFragment.requireContext(), 2)
            adapter = groupAdaptor
        }

        ui.pokemonsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                    viewModel.fetchNextPokemonList()
                }
            }
        })
    }

    private fun List<PokemonItem>.toPokemonCards(): List<PokemonListCard> {
        return this.map { pokemonItem -> PokemonListCard(pokemonItem) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }
}
