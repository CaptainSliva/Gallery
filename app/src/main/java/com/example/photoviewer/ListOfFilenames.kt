package com.example.photoviewer

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import java.io.File

// Это для тестов (просмотра имён файлов) класс был как и list_of_filenames.xml
var newContentResolver: ContentResolver? = null
class ListOfFilenames: Activity() {

    var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_of_filenames)

        var textView: TextView = findViewById(R.id.idText)
        var image: ImageView = findViewById(R.id.imageView)
        var bitmap: Bitmap? = null

        intent.extras!!.getStringArrayList("file")?.forEach { i ->
            textView.append("\n\n\n$i")
//            text.text = i
            newContentResolver = this.contentResolver
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, i.toUri())
        }
        image.setImageBitmap(bitmap)

//        val path = applicationContext.filesDir
//        val letDirectory = File(path, "PhotoViewerData")
//        val fileName = File(letDirectory, "$contentResolver.ini")
//        File(fileName).writeText(fileContent)





//        var k = 0
//        var j = 0
//        for (i in 0..<list[0].length) {
//            j++
//            if (list[0][i] == '/' && k<6)k++
//        }
//        textView.text = "${textView.text}\n\n\n${list[0]}"
//        File(list[0].slice(0..j)).walkTopDown().forEach { i ->
//            TODO("туду")
//        }







    }

}