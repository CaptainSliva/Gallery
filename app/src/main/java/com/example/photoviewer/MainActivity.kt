package com.example.photoviewer


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeColorStatusbar(R.color.violet, this, this.window)
        isDarkTheme(resources,Configuration(), findViewById<ConstraintLayout>(R.id.main), this)




        // initializing variables of grid view with their ids.
        val fotopath = listOf("Cyber Garden Drone (7-9 June 2024)")
        val albumGRV: GridView = findViewById(R.id.idAlbumsView)
        var albumList = ArrayList<GridViewModal>()
        val languages = listOf("Cyber Garden Drone (7-9 June 2024)")
        val images = listOf("AlbumsPreView/Vagner.png")
        itemModel = "album"

        // on below line we are adding data to
        // our course list with image and course name.
//        courseList += GridViewModal("C++", img)
//        courseList += GridViewModal("Java", R.drawable.rydra)
//        courseList += GridViewModal("Android", R.drawable.rydra)
//        courseList += GridViewModal("Python", R.drawable.rydra)
//        courseList += GridViewModal("Javascript", R.drawable.rydra)

        var bitMap = ArrayList<Bitmap>()


        for (i in images.indices) {
            try {
                //these images are stored in the root of "assets"
                bitMap += getBitmapFromAsset(images[i])
                Log.d("BITA", i.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val courseAdrapter = GridRVAdapter(courseList = albumList, this)


        for (i in languages.indices) {
            albumList += GridViewModal(languages[0], bitMap[0])
        }

        // on below line we are initializing our course adapter
        // and passing course list and context.
        val courseAdapter = GridRVAdapter(courseList = albumList, this)

        // on below line we are setting adapter to our grid view.
        albumGRV.adapter = courseAdapter


        // on below line we are adding on item
        // click listener for our grid view.
        albumGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // inside on click method we are simply displaying
            // a toast message with course name.
            Toast.makeText(
                applicationContext, albumList[position].courseName + " selected",
                Toast.LENGTH_SHORT
            ).show()
            val i = Intent(
                applicationContext,
                AlbumImagesActivity::class.java
            )

            // passing array index
            i.putExtra("albumPath", fotopath[position])
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

