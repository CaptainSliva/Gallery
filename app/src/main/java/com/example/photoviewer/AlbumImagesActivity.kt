
package com.example.photoviewer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class AlbumImagesActivity: Activity() {

    var albumPath = ""
    var imageName = ""
    val numberOfColumns = 4
    var isLoading = false
    var imagesCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        bitMap.clear()
        images.clear()
        itemModel = "image"

        val intent = intent
        val path = intent.extras!!.getString("albumPath")


        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_images)
        changeColorStatusbar(R.color.violet, this, this.window)
        isDarkTheme(resources, Configuration(), findViewById<ConstraintLayout>(R.id.images), this)

        Log.d("PrintUris", uris.toString())
        albumPath = path!!


        val newImage: ImageButton = findViewById(R.id.idNewImageBtn)
        val imageRV: RecyclerView = findViewById(R.id.idImageView)
        var recyclerDataArrayList = ArrayList<RecyclerData>()
        val layoutManager = GridLayoutManager(this, numberOfColumns)
        val adapter = RecyclerViewAdapter(recyclerDataArrayList, this@AlbumImagesActivity)

//        for (i in 0..10) {
//            addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, start = ct, stop = ct+4)
//            ct+=4
//        }
        addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, 0, 0)

        

//        imageRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//
//                if (!isLoading) {
//                    Log.i("Roll", "visibleItemCount : $visibleItemCount\ntotalItemCount : $totalItemCount" +
//                            "\nfirstVisibleItem : $firstVisibleItem\n lastVisibleItem : $lastVisibleItem")
//                    if ( (visibleItemCount+firstVisibleItem) >= totalItemCount) {
//                        ct = lastVisibleItem
//                        isLoading = true
//                        for ( i in lastVisibleItem+1..lastVisibleItem+4) {
//                            addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, lastVisibleItem, i)
//                        }
//                    }
//                }
//            }
//        })




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

                        // passing array index
                        i.putExtra("photoID", position)
                        i.putExtra("photoPath", images[position].toString())
                        i.putExtra("albumName", path)
                        startActivity(i)
                    }

                    override fun onLongClick(view: View, position: Int) {
                        FunctionsApp().deleteDialog( this@AlbumImagesActivity,
                            getString(R.string.title_image_delete), getString(R.string.messge_image_delete), images[position].toString(),
                            albumPath, recyclerDataArrayList, position, adapter
                        )
                    }

                })
        )


        newImage.setOnClickListener {
            addImages()
        }
    }

    override fun onResume() {
        super.onResume()
        itemModel = "image"
    }

//    @Throws(IOException::class)
//    private fun getBitmapFromAsset(strName: String): Bitmap {
//        val assetManager = assets
//
//        val istr = assetManager.open(strName)
//        val bitmap = BitmapFactory.decodeStream(istr)
//        istr.close()
//
//        return bitmap
//    }


    
    fun addItems(layoutManager: GridLayoutManager, adapter: RecyclerViewAdapter, path: String, recyclerDataArrayList: ArrayList<RecyclerData>, imageRV: RecyclerView, start: Int, stop: Int) {

        var stop = stop-1
//        var flag = false
        CoroutineScope(Dispatchers.Main).launch {
            val photos = withContext(Dispatchers.IO) { getPhotos(path) }
            imagesCount = photos.size
            Toast.makeText(applicationContext, "photos ${photos.size}", Toast.LENGTH_SHORT).show()
            stop = imagesCount-1 // Пока не работает загрузка по частям
            if (imagesCount < stop) {
                if (imagesCount-stop > 0) {
                    stop = start+(imagesCount-stop)
                }
                else {
                    stop = imagesCount-1
                }
            }
//            if (stop <= start) {
//                return@launch
//            }
            Log.i("first", start.toString())
            Log.i("last", stop.toString())
            for (i in start..stop) {
                if (images.size < imagesCount) {
//                    Log.d("PrintPhoto", photos[i].toString())
                    images.add(photos[i])
                    Log.i("Between", "$i|${images.size}")

                }

            }

            Toast.makeText(applicationContext, "images ${images.size}", Toast.LENGTH_SHORT).show()

            for (i in start..stop) {
                if (bitMap.size < imagesCount) {
                    try {
                        contentResolver.takePersistableUriPermission(
                            images[i],
                            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        )
                        val bmp = FunctionsImages().compressBitmap(
                            MediaStore.Images.Media.getBitmap(
                                this@AlbumImagesActivity.contentResolver,
                                images[i]
                            ), 500
                        )
                        bitMap.add(bmp)
                        recyclerDataArrayList.add(RecyclerData(i.toString(), bitMap[i]))
                        imageRV.layoutManager = layoutManager
                        imageRV.adapter = adapter
                    } catch (e: Exception) {
                        Toast.makeText(this@AlbumImagesActivity, e.toString(), Toast.LENGTH_LONG)
                            .show()
                        val hash = FunctionsImages().md5(
                            images[i].toString())
                        FunctionsFiles().deleteUnUseImages(applicationContext, albumPath, hash)
                        FunctionsFiles().deleteUnUseStorys(applicationContext, hash)
                    }
                }
            }

//            for (i in images.indices) {
//
//                try {
//                    contentResolver.takePersistableUriPermission(
//                        images[i],
//                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    )
//                    val bmp = compressBitmap(
//                        MediaStore.Images.Media.getBitmap(
//                            this@AlbumImagesActivity.contentResolver,
//                            images[i]
//                        ), 500
//                    )
//                    bitMap.add(bmp)
////                bmp = null
//                } catch (e: Exception) {
//                    Toast.makeText(this@AlbumImagesActivity, e.toString(), Toast.LENGTH_LONG).show()
//                }
//            }
//
//
//            for (i in images.indices) {
//                recyclerDataArrayList.add(RecyclerData(i.toString(), bitMap[i]))
//                imageRV.layoutManager = layoutManager
//                imageRV.adapter = adapter
//            }
        }
        isLoading = false
    }

    fun addImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, 1)
    }

    fun getPhotos(album: String): ArrayList<Uri> {
        val album = File(album)
//        Log.d("PrintAlbum", album.toString())
//        Log.d("PrintAlbum", album.readLines().toString())
        var retAlbum: ArrayList<Uri> = ArrayList()
        album.forEachLine { line ->
            retAlbum.add(line.split(delimiter)[0].toUri())
            Log.d("Print", retAlbum.toString())
        }
        return retAlbum
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        var fileList = ArrayList<String>()

        if (requestCode == 1) {


            if (resultData?.data != null) {
                Log.i("Path:", resultData.toString())
                fileList.add(resultData.data.toString())
            }

            if (resultData?.clipData != null) {
                val selectedImages = resultData!!.clipData
                Log.i("Path:", resultData.toString())
                for (i in 0..<selectedImages!!.itemCount) {
                    val path = selectedImages?.getItemAt(i)?.uri.toString()
                    fileList.add(path)
                    Log.d("PrintAddPh", path)

                }

            }
        }
        Log.d("PrintNoEmpty", "Is not Empty")
        FunctionsFiles().addPhotoToAlbumFile(applicationContext, albumPath, fileList)
        val i = Intent(
            applicationContext,
            AlbumImagesActivity::class.java)
        i.putExtra("albumPath", albumPath)
        startActivity(i)
        finish()

    }

}