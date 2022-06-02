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
import androidx.navigation.Navigation
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
import tech.borgranch.pokedex.ui.utils.verticalGradientDrawable

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var pokemonName: String = ""
    private var artwork: String = ""
    private var index: Int = 0
    private val viewModel by viewModels<DetailViewModel>()
    private var _ui: FragmentDetailBinding? = null
    private val ui get() = _ui!!
    private val navArgs by navArgs<DetailFragmentArgs>()
    private var groupAdaptor: GroupAdapter<GroupieViewHolder<ItemTypeBinding>>? = null
    private var abilityAdaptor: GroupAdapter<GroupieViewHolder<ItemTypeBinding>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
                    action.selectedIndex = this@DetailFragment.index - 1
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
            this.index = it.index + 1

            lifecycleScope.launchWhenResumed {
                bindUI()
                viewModel.getPokemonDetail(pokemonName)
            }
        }
    }

    private fun bindUI() {
        lifecycleScope.launchWhenResumed {
            viewModel.pokemonDetail.observe(viewLifecycleOwner) {
                it?.let { monsterDetail ->
                    ui.progressbar.visibility = View.GONE
                    ui.apply {
                        loadArtwork(artwork)
                        name.text = monsterDetail.name
                        index.text = this@DetailFragment.index.toString().padStart(4, '0')
                        height.text = monsterDetail.getHeightString()
                        weight.text = monsterDetail.getWeightString()
                    }

                    monsterDetail.types?.let { it ->
                        initTypeRecyclerView(it.toTypeItems())
                    }
                    monsterDetail.abilities?.let { it ->
                        initAbilityRecyclerView(it.toTypeItems())
                    }

                    ui.arrow.setOnClickListener { arrowView ->
                        val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
                        action.selectedIndex = this@DetailFragment.index - 1
                        Navigation.findNavController(arrowView).navigate(action)
                    }
                }
            }
        }
    }

    private fun initAbilityRecyclerView(items: List<TypeItem>) {
        abilityAdaptor = GroupAdapter<GroupieViewHolder<ItemTypeBinding>>().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            updateAsync(items)
        }
        ui.abilitiesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = abilityAdaptor
        }
    }

    private fun initTypeRecyclerView(typeCards: List<TypeItem>) {
        groupAdaptor = GroupAdapter<GroupieViewHolder<ItemTypeBinding>>().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            updateAsync(typeCards)
        }
        ui.typesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = groupAdaptor
        }
    }
    private fun loadArtwork(imageUrl: String) {
        Glide.with(requireContext())
            .load(imageUrl)
            .listener(
                GlidePalette.with(imageUrl)
                    .use(BitmapPalette.Profile.MUTED)
                    .intoCallBack { palette ->
                        val rgb = palette?.lightMutedSwatch?.rgb
                        val domain = palette?.darkMutedSwatch?.rgb
                        // val textColor = palette?.darkMutedSwatch?.titleTextColor
                        if (rgb != null && domain != null) {
                            ui.apply {
                                val gradientDrawable = verticalGradientDrawable(domain, rgb)
                                header.background = gradientDrawable
                                // index.setTextColor(textColor)
                                // arrow.drawable.setTint(textColor)
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
        groupAdaptor = null
        abilityAdaptor = null
        _ui = null
    }
}

private fun String.toTypeItems(): List<TypeItem> {
    return this.split(", ").map {
        TypeItem(it.trim())
    }
}
