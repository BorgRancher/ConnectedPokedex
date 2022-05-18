package tech.borgranch.pokedex.ui.main.list

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.xwray.groupie.viewbinding.BindableItem
import tech.borgranch.pokedex.R
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.databinding.PokemonItemBinding

class PokemonListCard(
    private val listedPokemon: PokemonItem
) : BindableItem<PokemonItemBinding>() {

    override fun bind(viewBinding: PokemonItemBinding, position: Int) {

        viewBinding.apply {

            Glide.with(viewBinding.root.context)
                .load(listedPokemon.artwork)
                .listener(
                    GlidePalette.with(listedPokemon.artwork)
                        .use(BitmapPalette.Profile.MUTED_LIGHT)
                        .intoCallBack { palette ->
                            val rgb = palette?.dominantSwatch?.rgb
                            val titleColor = palette?.dominantSwatch?.titleTextColor
                            if (rgb != null && titleColor != null) {
                                viewBinding.cardView.setCardBackgroundColor(rgb)
                                viewBinding.name.setTextColor(titleColor)
                            }
                        }.crossfade(true)
                )
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(image)
            name.text = listedPokemon.name
        }
    }

    override fun getLayout() = R.layout.pokemon_item

    override fun initializeViewBinding(view: View): PokemonItemBinding {
        return PokemonItemBinding.bind(view)
    }
}
