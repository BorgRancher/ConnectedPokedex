package tech.borgranch.pokedex.ui.main.detail

import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.xwray.groupie.viewbinding.BindableItem
import tech.borgranch.pokedex.R
import tech.borgranch.pokedex.databinding.ItemTypeBinding
import tech.borgranch.pokedex.utils.TypeUtils

class TypeItem(private val typeName: String) : BindableItem<ItemTypeBinding>() {
    override fun bind(viewBinding: ItemTypeBinding, position: Int) {
        viewBinding.typeName.text = typeName
        viewBinding.typeName.setTextAppearance(R.style.TextStyle)
        viewBinding.typeName.setTextColor(TypeUtils.getTypeColor(typeName))
        viewBinding.typeName.setBackgroundColor(ResourcesCompat.getColor(viewBinding.typeName.resources, R.color.white, null))
    }

    override fun getLayout() = R.layout.item_type

    override fun initializeViewBinding(view: View): ItemTypeBinding {
        return ItemTypeBinding.bind(view)
    }
}
