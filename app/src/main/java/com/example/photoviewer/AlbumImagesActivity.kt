
package com.example.photoviewer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
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
    val numberOfColumns = 4
    var imagesCount = 0
    var fileList: MutableList<Uri> = mutableListOf()
    var retFromAddImage = false
    val add = 4
    var stop = 19

    var recyclerDataArrayList = ArrayList<RecyclerDataImages>()
    val layoutManager = GridLayoutManager(this, numberOfColumns)
    val adapter = GridRVAdapterImages(recyclerDataArrayList, this@AlbumImagesActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (pageTransition.isEmpty()) FunctionsApp().initialPageTransition()

        images.clear()

        val intent = intent
        val path = intent.extras!!.getString("albumPath").toString()
//        FunctionsApp().editHat(path!!)



        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_images)
        changeColorStatusbar(R.color.violet, this, this.window)
        isDarkTheme(resources, Configuration(), findViewById<ConstraintLayout>(R.id.images), this)

        albumPath = path!!
        images = getPhotos(albumPath)

        val newImage: ImageButton = findViewById(R.id.idNewImageBtn)
        val imageRV = findViewById<RecyclerView>(R.id.idImageView)
        imageRV.layoutManager = layoutManager
        imageRV.adapter = adapter
//        var recyclerDataArrayList = ArrayList<RecyclerData>()
//        val layoutManager = GridLayoutManager(this, numberOfColumns)
//        val adapter = RecyclerViewAdapter(recyclerDataArrayList, this@AlbumImagesActivity)

//        for (i in 0..10) {
//            addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, start = ct, stop = ct+4)
//            ct+=4
//        }
        addItems(adapter, recyclerDataArrayList, 0, stop+13)
        stop+=13

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
        print("resume")
        print(fileList)
        if (retFromAddImage) {
            val hashes = FunctionsFiles().getHashesPhotosFromAlbum(applicationContext, albumPath)
            FunctionsFiles().addPhotoToAlbumFile(applicationContext, albumPath, fileList)

            fileList.forEach { image ->
                if (FunctionsImages().md5(image) !in hashes) {


                    val bmp = FunctionsImages().compressBitmap(
                        MediaStore.Images.Media.getBitmap(
                            this@AlbumImagesActivity.contentResolver,
                            image
                        ), 500
                    )

                    images.add(0, image)
                    recyclerDataArrayList.add(0, RecyclerDataImages(bmp))
                    adapter.notifyItemInserted(0)
                }
                print(image)
            }

            retFromAddImage = false
            fileList.clear()
        }
        if (pageTransition[2]) {
            recyclerDataArrayList.removeAt(intent.extras!!.getInt("removePosition"))
            adapter.notifyItemChanged(intent.extras!!.getInt("removePosition"))
        }
        FunctionsApp().initialPageTransition()

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


    fun addItems(adapter: GridRVAdapterImages, recyclerDataArrayList: ArrayList<RecyclerDataImages>, start: Int, stop: Int) {
        var stop = stop

        CoroutineScope(Dispatchers.Main).launch {
            val photos = withContext(Dispatchers.IO) { images }
            imagesCount = photos.size

            if (imagesCount < stop) {
                stop = imagesCount-1
                adapter.setNoMore(true) //если подгружать больше нечего
            }

            Log.i("first", start.toString())
            Log.i("last", stop.toString())
            if (stop == 0 && recyclerDataArrayList.isEmpty()) {
                try {
                    contentResolver.takePersistableUriPermission(
                        images[0],
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    )
                }
                catch (e: Exception) {
                    print("exept","EXEPTIOOOON")
                    return@launch}

                recyclerDataArrayList.add(RecyclerDataImages(FunctionsImages().compressBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this@AlbumImagesActivity.contentResolver,
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
                        print("exept","EXEPTIOOOON")
                        return@launch}


                    recyclerDataArrayList.add(RecyclerDataImages(FunctionsImages().compressBitmap(
                        MediaStore.Images.Media.getBitmap(
                            this@AlbumImagesActivity.contentResolver,
                            images[i]
                        ), 500
                    )))
                }
            }
            adapter.notifyItemRangeChanged(start, stop)
//            Toast.makeText(applicationContext, "images ${images.size}", Toast.LENGTH_SHORT).show()
        }
    }

    fun addImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, 1)
    }

    fun getPhotos(album: String): MutableList<Uri> {
        val album = File(album)
        var retAlbum: MutableList<Uri> = mutableListOf()

        album.forEachLine { line ->
            val uri = line.split(delimiterUriAndHash)[0].toUri()
            val hash = FunctionsImages().md5(uri)

            try {
                this.contentResolver.openInputStream(uri)
                retAlbum.add(uri)
            } catch (e: java.lang.Exception) {
                FunctionsFiles().deleteUnUseImages(applicationContext, albumPath, hash)
                FunctionsFiles().deleteUnUseStorys(applicationContext, hash)
            }
        }
        Toast.makeText(applicationContext, "photos ${retAlbum.size}", Toast.LENGTH_SHORT).show()
        return retAlbum
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        print(fileList)
        if (requestCode == 1) {


            if (resultData?.data != null) {
                fileList.add(0, resultData.data!!)
            }

            if (resultData?.clipData != null) {
                val selectedImages = resultData!!.clipData
                for (i in 0..<selectedImages!!.itemCount) {
                    val path = selectedImages?.getItemAt(i)?.uri!!
                    fileList.add(0, path)
                }
            }
        }
        Log.d("PrintNoEmpty", "Is not Empty")
        retFromAddImage = true


//        val i = Intent(
//            applicationContext,
//            AlbumImagesActivity::class.java)
//        i.putExtra("albumPath", albumPath)
//        startActivity(i)
//        finish()

    }

}