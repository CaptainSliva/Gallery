package com.example.photoviewer

import android.content.Context
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
    var strictSearch = true
    var textSearch = ""
    var findHash: MutableList<String> = mutableListOf()
    val add = 4
    var stop = 19

    override fun onCreate(savedInstanceState: Bundle?) {

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

        val imageRV: RecyclerView = findViewById(R.id.idImageView)
        var recyclerDataArrayList = ArrayList<RecyclerDataImages>()
        val layoutManager = GridLayoutManager(this, numberOfColumns)
        val adapter = GridRVAdapterImages(recyclerDataArrayList, this@SearchOnComment)
        imageRV.layoutManager = layoutManager
        imageRV.adapter = adapter


        adapter.setOnLoadMoreListener(object: OnLoadMoreListener {
            override fun onLoadMore() {
                addItems(adapter, recyclerDataArrayList, stop+1, stop+add)
                stop+=add
                adapter.endLoading() //когда загрузка завершена

            }
        })

        imageRV.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                imageRV,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {

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
                        FunctionsApp().deleteDialog( this@SearchOnComment,
                            getString(R.string.title_image_delete), getString(R.string.messge_image_delete), images[position].toString(),
                            albumsPaths[position], recyclerDataArrayList, position, adapter)
                        val i = Intent(
                            applicationContext,
                            AlbumImagesActivity::class.java)
                        i.putExtra("albumPath", albumsPaths[position])
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

                stop+=1
                addItems(adapter, recyclerDataArrayList,0, stop)
                stop+=1
                addItems(adapter, recyclerDataArrayList, stop, stop+11)
                stop+=11
                //addItems(adapter,recyclerDataArrayList, 0, 0)
           }
        })

        btnExit.setOnClickListener {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
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



            addItems(adapter, recyclerDataArrayList, 0, 0)

        }
    }


    fun addItems(adapter: GridRVAdapterImages, recyclerDataArrayList: ArrayList<RecyclerDataImages>, start: Int, stop: Int) {
        var stop = stop

//                Toast.makeText(applicationContext, "images ${images.size}", Toast.LENGTH_SHORT).show()
        if (images.size > 4) Toast.makeText(applicationContext, "Найдено ${images.size} изображений. Пожалйуста, подождите.", Toast.LENGTH_LONG).show()
        val imagesCount = images.size

        if (imagesCount < stop) {
            stop = imagesCount-1
            adapter.setNoMore(true) //если подгружать больше нечего
        }

        if (stop == 0 && recyclerDataArrayList.isEmpty()) {
            try {
                contentResolver.takePersistableUriPermission(
                    images[0],
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                )
            }
            catch (e: Exception) {
                print("exept","EXEPTIOOOON")
                return}

            recyclerDataArrayList.add(RecyclerDataImages(FunctionsImages().compressBitmap(
                MediaStore.Images.Media.getBitmap(
                    this@SearchOnComment.contentResolver,
                    images[0]
                ), 500
            )))
            adapter.notifyItemInserted(0)
        }


        for (i in start..stop) {
            if (recyclerDataArrayList.size < imagesCount) {
                try {
                    contentResolver.takePersistableUriPermission(
                        images[i],
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    )

                }
                catch (e: Exception) {
                    return
                }
                recyclerDataArrayList.add(RecyclerDataImages(FunctionsImages().compressBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this@SearchOnComment.contentResolver,
                        images[i]
                    ), 500
                )))
            }
        }
        adapter.notifyItemRangeChanged(start, stop)


        Log.d("PrintLog", "findHash: $findHash\nimages: $images\nalbums: $albumsPaths")
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


    fun clearAll(recyclerDataArrayList: ArrayList<RecyclerDataImages>, adapter: GridRVAdapterImages)
    {
        stop = 19
        findHash.clear()
        images.clear()
        recyclerDataArrayList.clear()
        adapter.notifyDataSetChanged()

    }

}