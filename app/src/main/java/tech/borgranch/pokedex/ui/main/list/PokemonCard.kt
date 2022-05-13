package tech.borgranch.pokedex.ui.main.list

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xwray.groupie.viewbinding.BindableItem
import tech.borgranch.pokedex.R
import tech.borgranch.pokedex.data.dto.PokemonItem
import tech.borgranch.pokedex.databinding.PokemonItemBinding

class PokemonCard(
    private val listedPokemon: PokemonItem
) : BindableItem<PokemonItemBinding>() {

    override fun bind(viewBinding: PokemonItemBinding, position: Int) {

        viewBinding.apply {
            Glide.with(viewBinding.root.context)
                .load(listedPokemon.artwork)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(ivMonsterAvatar)

            tvMonsterName.text = listedPokemon.name
        }
    }

    override fun getLayout() = R.layout.pokemon_item

    override fun initializeViewBinding(view: View): PokemonItemBinding {
        return PokemonItemBinding.bind(view)
    }
}
