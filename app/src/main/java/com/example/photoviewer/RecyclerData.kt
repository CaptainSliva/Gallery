package com.example.photoviewer

import android.graphics.Bitmap

class RecyclerData(val initial_title: String, val initial_img: Bitmap) {


    private var title: String? = null
    private var img: Bitmap? = null

    fun getTitle(): String {
        return initial_title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getImg(): Bitmap {
        return initial_img
    }

    fun setImg(imgid: Bitmap) {
        this.img = imgid
    }
}