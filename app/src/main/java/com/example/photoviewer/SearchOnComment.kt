package com.example.photoviewer

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

class SearchOnComment: AppCompatActivity() {

    var albumsPaths: MutableList<String> = mutableListOf()
    val numberOfColumns = 4
    var isLoading = false
    var imagesCount = 0
    var pos = 0
    var strictSearch = true
    var textSearch = ""
    var imageName = ""
    var findHash: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        itemModel = "image"

        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_on_comments)
        val search: EditText = findViewById(R.id.idSearch)
        val btnExit: Button = findViewById(R.id.idExitBtn)
        val btnRegx: ImageButton = findViewById(R.id.idBtnRegx)
        val textRegx: TextView = findViewById(R.id.idTextRegx)
        val textCountResults: TextView = findViewById(R.id.counter)

        changeColorStatusbar(R.color.violet, this, this.window)
        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK// Retrieve the Mode of the App.
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            search.setTextColor(this.resources.getColor(R.color.white))
            findViewById<ConstraintLayout>(R.id.search).setBackgroundColor(this.resources.getColor(R.color.black))
            textRegx.setTextColor(this.resources.getColor(R.color.white))
        }

        Log.d("PrintUris", uris.toString())

        val imageRV: RecyclerView = findViewById(R.id.idImageView)
        var recyclerDataArrayList = ArrayList<RecyclerData>()
        val layoutManager = GridLayoutManager(this, numberOfColumns)
        val adapter = RecyclerViewAdapter(recyclerDataArrayList, this@SearchOnComment)


        imageRV.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                imageRV,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        pos = position

                        val i = Intent(
                            applicationContext,
                            FullImageActivity::class.java
                        )

                        i.putExtra("photoID", position)
                        i.putExtra("photoPath", images[position].toString())
                        i.putExtra("albumName", albumsPaths[position])
                        startActivity(i)
                    }

                    override fun onLongClick(view: View, position: Int) {
                        pos = position

                        FunctionsApp().deleteDialog( this@SearchOnComment,
                            getString(R.string.title_image_delete), getString(R.string.messge_image_delete), images[position].toString(),
                            albumsPaths[position], recyclerDataArrayList, position, adapter
                        )
                        val i = Intent(
                            applicationContext,
                            AlbumImagesActivity::class.java)
                        i.putExtra("albumPath", albumsPaths[pos])
                        startActivity(i)
                        finish()
                    }
                })
        )


        search.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                clearAll(recyclerDataArrayList, adapter)
                searchPhotoOnStory(s.toString())
                textSearch = s.toString()
                textCountResults.text = "Найдено: ${images.size}"
                // TODO Добавление элементов (правда пока не работает, надо разобраться. Немного работает
                //  Надо понять как обновлять и ещё хэшей почему-то находит больше чем изображений для них.
                //  Пофиксить↑)
                addItems(layoutManager, adapter, albumsPaths, recyclerDataArrayList, imageRV, 0, 0)
            }
        })

        btnExit.setOnClickListener {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
        }

        btnRegx.setOnClickListener {
            clearAll(recyclerDataArrayList, adapter)
            if (strictSearch) {
                btnRegx.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_circle_16dp))
                if (isDarkModeOn ) {
                    textRegx.setTextColor(this.resources.getColor(R.color.translucent1))
                }
                else {
                    textRegx.setTextColor(this.resources.getColor(R.color.translucent))
                }
                strictSearch = !strictSearch
            }
            else {
                btnRegx.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_circle_outline_16dp))
                if (isDarkModeOn ) {
                    textRegx.setTextColor(this.resources.getColor(R.color.white))
                }
                else {
                    textRegx.setTextColor(this.resources.getColor(R.color.black))
                }
                strictSearch = !strictSearch
            }

            searchPhotoOnStory(textSearch)
            textCountResults.text = "Найдено: ${images.size}"
            addItems(layoutManager, adapter, albumsPaths, recyclerDataArrayList, imageRV, 0, 0)


        }
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        itemModel = "image"
    }

    fun addItems(layoutManager: GridLayoutManager, adapter: RecyclerViewAdapter, paths: MutableList<String>, recyclerDataArrayList: ArrayList<RecyclerData>, imageRV: RecyclerView, start: Int, stop: Int) {


//                Toast.makeText(applicationContext, "images ${images.size}", Toast.LENGTH_SHORT).show()
        if (images.size > 4) Toast.makeText(applicationContext, "Найдено ${images.size} изображений. Пожалйуста, подождите.", Toast.LENGTH_LONG).show()

        for (i in 0..images.size-1) {
            try {
                contentResolver.takePersistableUriPermission(
                    images[i],
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                )
                val bmp = FunctionsImages().compressBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this@SearchOnComment.contentResolver,
                        images[i]
                    ), 500
                )
                bitMap.add(bmp)
                recyclerDataArrayList.add(RecyclerData(i.toString(), bitMap[i]))
                imageRV.layoutManager = layoutManager
                imageRV.adapter = adapter
            } catch (e: Exception) {
//                Toast.makeText(this@SearchOnComment, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

        Log.d("PrintLog", "findHash: $findHash\nimages: $images")

        isLoading = false
    }

    fun getPhotos(album: String): ArrayList<Uri> {
        val album = File(album)
        var retAlbum: ArrayList<Uri> = ArrayList()
        album.forEachLine { line ->
            retAlbum.add(line.split(delimiter)[0].toUri())
            Log.d("Print", retAlbum.toString())
        }
        return retAlbum
    }

    fun searchPhotoOnStory(s: String) {
        if (s.isNotEmpty()) {
            // Буква появилась → пошел поиск по файлам каталога (и пока идёт вывод найденных
            // историй в лог)
            val path = applicationContext.filesDir
            val fileStorys = File(path, "$storysFolder/$storyAlbumName.ini")
            fileStorys.readLines().forEach { line ->
                var story = line.split(delimiterPhotoAndStory)[1].replace(nNewLine, "\n")
                var hash = line.split(delimiterPhotoAndStory)[0]
                if (strictSearch) {

                    if (s in story) {
                        if (hash !in findHash) {
                            findHash.add(hash)
                        }
                    }
                }
                else {
                    story = story.lowercase()
                    if (s.lowercase() in story) {
                        if (hash !in findHash) {
                            findHash.add(hash)
                        }
                    }
                }
            }
            albumsPaths = FunctionsFiles().findImageAlbumOnHash(applicationContext, findHash)
            images = FunctionsFiles().findImagesOnHash(applicationContext, findHash)
        }
    }


    fun clearAll(recyclerDataArrayList: ArrayList<RecyclerData>, adapter: RecyclerViewAdapter)
    {
        bitMap.clear()
        findHash.clear()
        images.clear()
        recyclerDataArrayList.clear()
        adapter.notifyDataSetChanged()

    }

}