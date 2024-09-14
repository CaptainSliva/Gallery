package com.example.photoviewer

import android.graphics.Bitmap

class RecyclerDataImages(val img: Bitmap) {

    private var image: Bitmap? = null
//    private var album = album_name

    fun getImage(): Bitmap? {
        return img
    }

    fun setImage(image: Bitmap) {
        this.image = image
    }

//    fun getTitle() : String? {
//        return album_name
//    }
//
//    fun setTitle(album_name: String) {
//        this.album = album_name
//    }
}