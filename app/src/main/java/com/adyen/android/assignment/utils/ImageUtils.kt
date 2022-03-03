package com.adyen.android.assignment.utils

import android.content.Context

object ImageUtils {

    fun getIconSize(context: Context): String {
        val density: Float = context.resources.displayMetrics.density
        return when {
            density >= 4.0 -> "120"  // xxxhdpi
            density >= 3.0 -> "88"   // xxhdpi
            density >= 2.0 -> "64"   // xhdpi
            density >= 1.5 -> "44"   // hdpi
            else -> "32"             // mdpi
        }
    }

}