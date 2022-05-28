package tech.borgranch.pokedex.ui.main.detail

import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.viewbinding.BindableItem
import tech.borgranch.pokedex.R
import tech.borgranch.pokedex.databinding.ItemTypeBinding
import tech.borgranch.pokedex.utils.TypeUtils

// Neat little cards to indicate a pokemon's type(s)
class TypeItem(private val typeName: String) : BindableItem<ItemTypeBinding>() {
    override fun bind(viewBinding: ItemTypeBinding, position: Int) {
        viewBinding.apply {
            typeName.text = this@TypeItem.typeName
            typeName.setTextAppearance(R.style.TextStyle)
            val typeColor = TypeUtils.getTypeColor(this@TypeItem.typeName)
            typeCard.setCardBackgroundColor(ContextCompat.getColor(this.root.context, typeColor))
            typeCard.radius = this.root.context.resources.getDimension(R.dimen.card_radius)
        }
    }

    override fun getLayout() = R.layout.item_type

    override fun initializeViewBinding(view: View): ItemTypeBinding {
        return ItemTypeBinding.bind(view)
    }
}
