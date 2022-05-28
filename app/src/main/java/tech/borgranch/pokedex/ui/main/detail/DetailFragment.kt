package tech.borgranch.pokedex.ui.main.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.viewbinding.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import tech.borgranch.pokedex.databinding.FragmentDetailBinding
import tech.borgranch.pokedex.databinding.ItemTypeBinding
import tech.borgranch.pokedex.ui.main.MainActivity
import java.net.URI

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var pokemonName: String = ""
    private var artwork: String = ""
    private var index: Int = 0

    companion object {
        fun newInstance() = DetailFragment()
    }

    private val viewModel by viewModels<DetailViewModel>()
    private var _ui: FragmentDetailBinding? = null
    private val ui get() = _ui!!
    private val navArgs by navArgs<DetailFragmentArgs>()
    private var groupAdaptor: GroupAdapter<GroupieViewHolder<ItemTypeBinding>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
                    action.selectedIndex = this@DetailFragment.index
                    this@DetailFragment.findNavController().navigate(action)
                }
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).toolbarHide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = FragmentDetailBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onStart() {
        super.onStart()
        navArgs.let {
            this.pokemonName = it.name
            this.artwork = it.artwork
            this.index = it.index

            lifecycleScope.launchWhenResumed {
                viewModel.getPokemonDetail(pokemonName)
                bindUI()
            }
        }
    }

    private fun bindUI() {
        viewModel.pokemonDetail.observe(viewLifecycleOwner) {
            it?.let { pokemonDetail ->
                ui.apply {
                    loadArtwork()
                    name.text = pokemonDetail.name
                }
                ui.progressbar.visibility = View.GONE
                ui.height.text = pokemonDetail.getHeightString()
                ui.weight.text = pokemonDetail.getWeightString()
                pokemonDetail.types?.let { it ->
                    initTypeRecyclerView(it.toTypeItems())
                }
            }
        }
    }

    private fun initTypeRecyclerView(typeCards: List<TypeItem>) {
        groupAdaptor = GroupAdapter<GroupieViewHolder<ItemTypeBinding>>().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            addAll(typeCards)
        }
        ui.typesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = groupAdaptor
        }
    }
    private fun loadArtwork() {
        Glide.with(requireContext())
            .load(artwork)
            .listener(
                GlidePalette.with(artwork)
                    .use(BitmapPalette.Profile.VIBRANT_LIGHT)
                    .intoCallBack { palette ->
                        val rgb = palette?.dominantSwatch?.rgb
                        if (rgb != null) {
                            ui.apply {
                                image.setBackgroundColor(rgb)
                                header.setBackgroundColor(rgb)
                            }
                        }
                    }.crossfade(true)
            )
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(ui.image)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).toolbarShow()
        _ui = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: URI)
    }
}

private fun String.toTypeItems(): List<TypeItem> {
    return this.split(",").map {
        TypeItem(it)
    }
}
