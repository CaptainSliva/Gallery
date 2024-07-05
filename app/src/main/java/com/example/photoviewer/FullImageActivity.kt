package com.example.photoviewer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.setMargins
import com.github.chrisbanes.photoview.PhotoView

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
        var f = false


        Log.d("Size", "${bitMap[position].width} x ${bitMap[position].height}")

        val photoView: PhotoView = findViewById(R.id.full_image_view)
        photoView.setImageBitmap(bitMap[position])
        val margins: MarginLayoutParams = photoView.layoutParams as ViewGroup.MarginLayoutParams


        bRotate.setOnClickListener {
            Log.d("Print", isPortrait.toString())
            requestedOrientation = if (isPortrait) {
                f = true
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            } else {
                f = false
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            }
            isPortrait = !isPortrait
            Log.d("Print", isPortrait.toString())

            if (f) {
                margins.setMargins(0, 300, 0 ,0)
                photoView.layoutParams = margins
            }
            else {
                margins.setMargins(0, 0, 300 ,0)
                photoView.layoutParams = margins
            }
        }

    }

}