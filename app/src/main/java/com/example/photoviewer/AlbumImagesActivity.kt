
package com.example.photoviewer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
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

    var albumName = ""
    val numberOfColumns = 4
    var isLoading = false
    var imagesCount = 0
    var fileList: MutableList<String> = mutableListOf()
    var retFromAddImage = false
    lateinit var allPhotos: MutableList<Uri>
    val add = 20
    var stop = add
    var path = ""

    lateinit var imageRV: RecyclerView
    var recyclerDataArrayList = ArrayList<RecyclerDataImages>()
    val layoutManager = GridLayoutManager(this, numberOfColumns)
    val adapter = GridRVAdapterImages(recyclerDataArrayList, this@AlbumImagesActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (pageTransition.isEmpty()) FunctionsApp().initialPageTransition()

        images.clear()

        val intent = intent
        path = intent.extras!!.getString("albumName").toString()
//        FunctionsApp().editHat(path!!)
        allPhotos = getPhotos(path)



        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_images)
        changeColorStatusbar(R.color.violet, this, this.window)
        isDarkTheme(resources, Configuration(), findViewById<ConstraintLayout>(R.id.images), this)

        albumName = path!!


        val newImage: ImageButton = findViewById(R.id.idNewImageBtn)
        imageRV = findViewById(R.id.idImageView)
        imageRV.layoutManager = layoutManager
        imageRV.adapter = adapter
//        var recyclerDataArrayList = ArrayList<RecyclerData>()
//        val layoutManager = GridLayoutManager(this, numberOfColumns)
//        val adapter = RecyclerViewAdapter(recyclerDataArrayList, this@AlbumImagesActivity)

//        for (i in 0..10) {
//            addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, start = ct, stop = ct+4)
//            ct+=4
//        }
        addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, 0, stop)

        adapter.setOnLoadMoreListener(object: OnLoadMoreListener {
            override fun onLoadMore() {
                addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, stop, stop+add)
                stop+=20
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
                            albumName, recyclerDataArrayList, position, adapter
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
        if (retFromAddImage) {
            val hashes = FunctionsFiles().getHashesPhotosFromAlbum(applicationContext, albumName)
            FunctionsFiles().addPhotoToAlbumFile(applicationContext, albumName, fileList)
            fileList.forEach { image ->
                if (FunctionsImages().md5(image.toUri()) !in hashes) {


                    val bmp = FunctionsImages().compressBitmap(
                        MediaStore.Images.Media.getBitmap(
                            this@AlbumImagesActivity.contentResolver,
                            image.toUri()
                        ), 500
                    )

                    images.add(0, image.toUri())
                    recyclerDataArrayList.add(0, RecyclerDataImages(bmp))
                    adapter.notifyItemChanged(0)
                }
            }
            retFromAddImage = false
            fileList.clear()
        }
        else{
            if (recyclerDataArrayList.size < stop && allPhotos.size > stop) {
                addItems(layoutManager, adapter, path, recyclerDataArrayList, imageRV, stop, stop+add)
                stop+=20
            }
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


    fun addItems(layoutManager: GridLayoutManager, adapter: GridRVAdapterImages, path: String, recyclerDataArrayList: ArrayList<RecyclerDataImages>, imageRV: RecyclerView, start: Int, stop: Int) {
        var stop = stop

        CoroutineScope(Dispatchers.Main).launch {
            val photos = withContext(Dispatchers.IO) { allPhotos }
            imagesCount = photos.size

            if (stop == 0) stop = imagesCount-1
            if (imagesCount < stop) {
                stop = imagesCount-1
                adapter.setNoMore(true) //если подгружать больше нечего
            }

            Log.i("first", start.toString())
            Log.i("last", stop.toString())
            for (i in start..stop) {
                if (images.size < imagesCount) {
                    images.add(photos[i])

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
                    recyclerDataArrayList.add(RecyclerDataImages(bmp))
                }
            }
            Toast.makeText(applicationContext, "images ${images.size}", Toast.LENGTH_SHORT).show()
            adapter.notifyDataSetChanged()
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

    fun getPhotos(album: String): MutableList<Uri> {
        val album = File(album)
//        Log.d("PrintAlbum", album.toString())
//        Log.d("PrintAlbum", album.readLines().toString())
        var retAlbum: MutableList<Uri> = mutableListOf()

        album.forEachLine { line ->
            val uri = line.split(delimiterUriAndHash)[0].toUri()
            val hash = FunctionsImages().md5(uri)

            try {
                this.contentResolver.openInputStream(uri)
                retAlbum.add(line.split(delimiterUriAndHash)[0].toUri())
            } catch (e: java.lang.Exception) {
                FunctionsFiles().deleteUnUseImages(applicationContext, albumName, hash)
                FunctionsFiles().deleteUnUseStorys(applicationContext, hash)
            }
        }
        Toast.makeText(applicationContext, "photos ${retAlbum.size}", Toast.LENGTH_SHORT).show()
        return retAlbum
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

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
        retFromAddImage = true


//        val i = Intent(
//            applicationContext,
//            AlbumImagesActivity::class.java)
//        i.putExtra("albumPath", albumPath)
//        startActivity(i)
//        finish()

    }

}