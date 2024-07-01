package com.example.photoviewer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd

var isPortrait = true
class FullImageActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

//        val w: Window = window

        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_image)

        val intent = intent
        val position = intent.extras!!.getInt("photoID")
        val bRotate: Button = findViewById(R.id.idRotateButton)


        Log.d("Size", "${bitMap[position].width} x ${bitMap[position].height}")

        val imageView: ImageView = findViewById(R.id.full_image_view)
        imageView.setImageBitmap(bitMap[position])


        bRotate.setOnClickListener {
            Log.d("Print", isPortrait.toString())
            requestedOrientation = if (isPortrait) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                // else change to Portrait
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isPortrait = !isPortrait
            Log.d("Print", isPortrait.toString())
        }

    }

}