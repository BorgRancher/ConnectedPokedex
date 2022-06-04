package tech.borgranch.pokedex.ui.utils

import android.graphics.drawable.GradientDrawable

fun verticalGradientDrawable(startColor: Int, endColor: Int): GradientDrawable {
    val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor))
    gradientDrawable.cornerRadius = 14.0f
    return gradientDrawable
}
