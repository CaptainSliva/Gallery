package com.example.photoviewer

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint

class UIcolorChange {
}
fun changeColorStatusbar(color: Int, context: Context, window: Window) {
    window.statusBarColor = context.resources.getColor(color)
}
fun isDarkTheme(resources: Resources, configuration: Configuration, view: ConstraintLayout, context: Context) {
    val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK// Retrieve the Mode of the App.
    val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
    if (isDarkModeOn) {
        view.setBackgroundColor(context.resources.getColor(R.color.black))
    }
}

