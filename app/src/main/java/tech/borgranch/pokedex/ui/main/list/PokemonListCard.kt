package tech.borgranch.pokedex.ui.main.list

import android.view.View
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.xwray.groupie.viewbinding.BindableItem
import tech.borgranch.pokedex.R
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.databinding.ItemPokemonBinding
import tech.borgranch.pokedex.ui.utils.verticalGradientDrawable

class PokemonListCard(
    private val listedPokemon: PokemonItem
) : BindableItem<ItemPokemonBinding>() {

    override fun bind(viewBinding: ItemPokemonBinding, position: Int) {

        viewBinding.apply {

            Glide.with(viewBinding.root.context)
                .load(listedPokemon.artwork)
                .listener(
                    // Load a complimentary background color for the artwork
                    GlidePalette.with(listedPokemon.artwork)
                        .use(BitmapPalette.Profile.MUTED)
                        .intoCallBack { palette ->
                            val muted = palette?.lightMutedSwatch?.rgb
                            val darkMuted = palette?.darkMutedSwatch?.rgb
                            // Ensure that the text on the card is readable
                            if (muted != null && darkMuted != null) {
                                val gradientDrawable = verticalGradientDrawable(muted, darkMuted)
                                viewBinding.pokemonHolder.background = gradientDrawable
                            } else if (darkMuted != null) {
                                val gradientDrawable = verticalGradientDrawable(darkMuted, darkMuted)
                                viewBinding.pokemonHolder.background = gradientDrawable
                            }
                        }.crossfade(true)
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image)
            name.text = listedPokemon.name
        }

        // Clicking on the card will navigate to the details screen
        viewBinding.cardView.setOnClickListener {
            val directions =
                ListFragmentDirections.actionListFragmentToDetailFragment()
            directions.name = listedPokemon.name
            directions.artwork = listedPokemon.artwork
            directions.index = position
            Navigation.findNavController(it).navigate(directions)
        }
    }

    override fun getLayout() = R.layout.item_pokemon

    override fun initializeViewBinding(view: View): ItemPokemonBinding {
        return ItemPokemonBinding.bind(view)
    }
}
