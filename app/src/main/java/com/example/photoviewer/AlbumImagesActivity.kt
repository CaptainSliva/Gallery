package com.example.photoviewer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.IOException


public var bitMap = ArrayList<Bitmap>()

class AlbumImagesActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        bitMap.clear()
        itemModel = "image"

        val intent = intent
        val path = intent.extras!!.getString("albumPath")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_images)
        changeColorStatusbar(R.color.violet, this, this.window)
        isDarkTheme(resources,Configuration(), findViewById<ConstraintLayout>(R.id.imges), this)






        // initializing variables of grid view with their ids.
        val fotopath = path
        val imageGRV: GridView = findViewById(R.id.idImageView)
        var imageList = ArrayList<GridViewModal>()
        val images = listOf(
            "$fotopath/IMG_20240608_234836.jpg", "$fotopath/IMG_20240608_143957.jpg", "$fotopath/IMG_20240609_152514.jpg",
            "$fotopath/IMG_20240610_002927.jpg", "$fotopath/IMG_20240609_234959.jpg", "$fotopath/IMG_20240607_144222.jpg",
            )



        for (i in images.indices) {
            try {
                //these images are stored in the root of "assets"
                bitMap += getBitmapFromAsset(images[i])
                Log.d("BITA", i.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        for (i in images.indices) {
            imageList += GridViewModal(i.toString(), bitMap[i])
        }

        // on below line we are initializing our course adapter
        // and passing course list and context.
        val courseAdapter = GridRVAdapter(courseList = imageList, this)

        // on below line we are setting adapter to our grid view.
        imageGRV.adapter = courseAdapter


        // on below line we are adding on item
        // click listener for our grid view.
        imageGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // inside on click method we are simply displaying
            // a toast message with course name.
            Toast.makeText(
                applicationContext, "photo " + imageList[position].courseName + " selected",
                Toast.LENGTH_SHORT
            ).show()
            val i = Intent(
                applicationContext,
                StoryImageActivity::class.java
            )

            // passing array index
            i.putExtra("photoID", position)
            i.putExtra("photoName", images[position])
            i.putExtra("photoPath", path)
            startActivity(i)
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromAsset(strName: String): Bitmap {
        val assetManager = assets

        val istr = assetManager.open(strName)
        val bitmap = BitmapFactory.decodeStream(istr)
        istr.close()

        return bitmap
    }
}