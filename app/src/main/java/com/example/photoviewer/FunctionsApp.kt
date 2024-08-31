package com.example.photoviewer

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class FunctionsApp: AppCompatActivity() {

    var imageName = ""

//    fun deleteDialog(context: Context, title: String, message: String, imageDeleteName: String, albumPath: String) {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle(title)
//        builder.setMessage("$message?")
//        imageName = FunctionsFiles().albumName(imageDeleteName)
//        builder.setPositiveButton(
//            "ОК"
//        ) { dialog, which -> imageName = imageDeleteName
//            FunctionsFiles().deletePhoto(context, albumPath, imageDeleteName)
//
//        }
//
//        builder.setNegativeButton(
//            "ОТМЕНА"
//        ) { dialog, which -> dialog.cancel() }
//        builder.show()
//    }

    fun getRealPathFromURI(contentURI: Uri): String {
        var result: String
        var cursor: Cursor? =
            this?.contentResolver?.query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath().toString();
        } else {
            cursor.moveToFirst();
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    fun deleteDialog(context: Context, title: String, message: String, imageDeleteName: String, albumPath: String,
                     recyclerDataArrayList: ArrayList<RecyclerData>, position: Int, adapter: RecyclerViewAdapter) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage("$message?")
        imageName = FunctionsFiles().albumName(imageDeleteName)
        builder.setPositiveButton(
            "ОК"
        ) { dialog, which -> imageName = imageDeleteName
            FunctionsFiles().deletePhoto(context, albumPath, imageDeleteName)
            recyclerDataArrayList.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        builder.setNegativeButton(
            "ОТМЕНА"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    //Удаление альбома
    fun deleteAlbumDialog(context: Context, title: String, message: String, albumDeleteName: String, albumName: String,
                          recyclerDataArrayList: ArrayList<RecyclerData>, position: Int, adapter: RecyclerViewAdapter) {
        var albumName = albumName
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage("$message \"${FunctionsFiles().albumName(albumDeleteName)}\"?")

        builder.setPositiveButton(
            "ОК"
        ) { dialog, which -> albumName = albumDeleteName
            FunctionsFiles().deleteAlbum(context, albumName)
            recyclerDataArrayList.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        builder.setNegativeButton(
            "ОТМЕНА"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }



//    fun addItems(context: Context, layoutManager: GridLayoutManager, adapter: RecyclerViewAdapter, path: String, recyclerDataArrayList: ArrayList<RecyclerData>, imageRV: RecyclerView, start: Int, stop: Int) {
//
//        var stop = stop-1
////        var flag = false
//        CoroutineScope(Dispatchers.Main).launch {
//            val photos = withContext(Dispatchers.IO) { FunctionsFiles().getPhotos(path) }
//            var imagesCount = photos.size
//            Toast.makeText(context, "photos ${photos.size}", Toast.LENGTH_SHORT).show()
//            stop = imagesCount-1 // Пока не работает загрузка по частям
//            if (imagesCount < stop) {
//                if (imagesCount-stop > 0) {
//                    stop = start+(imagesCount-stop)
//                }
//                else {
//                    stop = imagesCount-1
//                }
//            }
////            if (stop <= start) {
////                return@launch
////            }
//            Log.i("first", start.toString())
//            Log.i("last", stop.toString())
//            for (i in start..stop) {
//                if (images.size < imagesCount) {
////                    Log.d("PrintPhoto", photos[i].toString())
//                    images.add(photos[i])
//                    Log.i("Between", "$i|${images.size}")
//
//                }
//
//            }
//
//            Toast.makeText(context, "images ${images.size}", Toast.LENGTH_SHORT).show()
//
//            for (i in start..stop) {
//                if (bitMap.size < imagesCount) {
//                    try {
//                        contentResolver.takePersistableUriPermission(
//                            images[i],
//                            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                        )
//                        val bmp = FunctionsImages().compressBitmap(
//                            MediaStore.Images.Media.getBitmap(
//                                context.contentResolver,
//                                images[i]
//                            ), 500
//                        )
//                        bitMap.add(bmp)
//                        recyclerDataArrayList.add(RecyclerData(i.toString(), bitMap[i]))
//                        imageRV.layoutManager = layoutManager
//                        imageRV.adapter = adapter
//                    } catch (e: Exception) {
//                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
//                            .show()
//                    }
//                }
//            }
//
////            for (i in images.indices) {
////
////                try {
////                    contentResolver.takePersistableUriPermission(
////                        images[i],
////                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
////                    )
////                    val bmp = compressBitmap(
////                        MediaStore.Images.Media.getBitmap(
////                            this@AlbumImagesActivity.contentResolver,
////                            images[i]
////                        ), 500
////                    )
////                    bitMap.add(bmp)
//////                bmp = null
////                } catch (e: Exception) {
////                    Toast.makeText(this@AlbumImagesActivity, e.toString(), Toast.LENGTH_LONG).show()
////                }
////            }
////
////
////            for (i in images.indices) {
////                recyclerDataArrayList.add(RecyclerData(i.toString(), bitMap[i]))
////                imageRV.layoutManager = layoutManager
////                imageRV.adapter = adapter
////            }
//        }
////        isLoading = false
//    }

}

fun print(a: Any) {
    Log.d("Print", a.toString())
}

fun print(tag: String, a: Any) {
    Log.d(tag, a.toString())
}

fun Tost(context: Context, s: Any) {
    Toast.makeText(context, s.toString(), Toast.LENGTH_SHORT).show()
}