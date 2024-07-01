package com.example.photoviewer

import android.graphics.Bitmap

// on below line we are creating a modal class.
data class GridViewModal(
    // we are creating a modal class with 2 member
    // one is course name as string and
    // other course img as int.
    val courseName: String,
    val courseImg: Bitmap
)
