package com.example.photoviewer

import android.graphics.Bitmap

class RecyclerDataAlbums(val initial_title: String, val initial_img: Bitmap) {


    private var title: String? = null
    private var aimg: Bitmap? = null

    fun getTitle(): String? {
        return initial_title
    }

    fun getImgeAlbum(): Bitmap? {
        return initial_img
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setImgAlbum(imgid: Bitmap?) {
        this.aimg = imgid
    }
}