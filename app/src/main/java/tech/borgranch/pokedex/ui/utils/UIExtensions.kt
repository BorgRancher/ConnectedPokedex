package tech.borgranch.pokedex.ui.utils

import android.graphics.drawable.GradientDrawable

// GradientDrawables are used to create a gradient background for a view
fun verticalGradientDrawable(startColor: Int, endColor: Int): GradientDrawable {
    val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor))
    return gradientDrawable
}
