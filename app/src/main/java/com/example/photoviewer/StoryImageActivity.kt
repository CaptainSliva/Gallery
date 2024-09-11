package com.example.photoviewer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date


class StoryImageActivity: Activity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        if(pageTransition[0] && pageTransition[1]) finish()


        val intent = intent
        val albumPath = intent.extras!!.getString("albumName")!!
        val photoPath = intent.extras!!.getString("photoPath").toString()
        val position = intent.extras!!.getInt("photoID")
        var editFlag = false
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_story)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val imageStory: ImageView = findViewById(R.id.idImage)
        val textViewStory: EditText = findViewById(R.id.idEditText)
        val textViewDate: TextView = findViewById(R.id.idDate)
        val buttonEdit: Button = findViewById(R.id.idEditButton)

//        changeColorStatusbar(R.color.white, this, this.window)
        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK// Retrieve the Mode of the App.
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            changeColorStatusbar(R.color.black, this, this.window)
            textViewStory.setTextColor(this.resources.getColor(R.color.white))
            textViewDate.setTextColor(this.resources.getColor(R.color.white))
            findViewById<ConstraintLayout>(R.id.storyy).setBackgroundColor(this.resources.getColor(R.color.black))
        }
        else {
            changeColorStatusbar(R.color.white, this, this.window)
        }


        val path = applicationContext.filesDir
        val letDirectory = File(path, storysFolder)
        if (!letDirectory.exists()) {
            letDirectory.mkdir()
        }
        val fileStory = File(letDirectory, "$storyAlbumName.ini")

        imageStory.setImageBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, images[position]))

                    textViewStory.setText(FunctionsFiles().readStory(applicationContext, fileStory, photoPath, fileStorys))


        // Получение даты фото
        textViewDate.text = photoPath
        try {
            var imageFile = File(photoPath.toUri()?.let { FunctionsApp().getRealPathFromURI(it) })
            val lastModifiedDate =  Date(imageFile.lastModified())
            val dateFormat = SimpleDateFormat("dd.MM.yyyy\t\tHH:mm")
            val formattedDate = dateFormat.format(lastModifiedDate)
            textViewDate.text = formattedDate
        }
        catch (e: Exception) {

        }




        buttonEdit.setOnClickListener {
            editFlag = !editFlag
            if (editFlag) {
                textViewStory.isFocusableInTouchMode = true
                buttonEdit.text = "Сохранить"
            }
            else {
                textViewStory.isFocusableInTouchMode = false
                buttonEdit.text = "Редактировать"
                fileStorys[FunctionsImages().md5(photoPath)] = textViewStory.text.toString().replace("\n", nNewLine)
                Log.d("Print", "${FunctionsImages().md5(photoPath)} : ${textViewStory.text}")
                val path = applicationContext.filesDir
                val letDirectory = File(path, storysFolder)
                val fileStory = File(letDirectory, "$storyAlbumName.ini")
//        Log.d("Print", "${fileStorys.keys.size}")
//        for (i in fileStorys.keys)
//        {
//            Log.d("Print", "$i=${fileStorys[i]}\n")
//        }

                fileStory.printWriter().use {
                        out ->
                    for (i in fileStorys.keys)
                    {
                        val wrt = "$i$delimiterPhotoAndStory${fileStorys[i]}"
                        out.println(wrt)
                        Log.d("PrintEND", wrt)
                    }
                }
            }
        }

        imageStory.setOnClickListener {

            fileStorys[FunctionsImages().md5(photoPath)] = textViewStory.text.toString().replace("\n", nNewLine)
            Log.d("Print", "${FunctionsImages().md5(photoPath)} : ${textViewStory.text}")
            val path = applicationContext.filesDir
            val letDirectory = File(path, storysFolder)
            val fileStory = File(letDirectory, "$storyAlbumName.ini")

            fileStory.printWriter().use {
                    out ->
                for (i in fileStorys.keys)
                {
                    val wrt = "$i$delimiterPhotoAndStory${fileStorys[i]}"
                    out.println(wrt)
                    Log.d("PrintEND", wrt)
                }
            }
            pageTransition[0] = true
            val i = Intent(
                applicationContext,
                FullImageActivity::class.java
            )

            // passing array index
            i.putExtra("photoID", position)
            i.putExtra("photoPath", photoPath)
            i.putExtra("albumName", albumPath)
            startActivity(i)
        }

    }


    override fun onResume() {
        super.onResume()
        if (pageTransition[2]) finish()
    }

//    override fun onStop() {
//        val path = applicationContext.filesDir
//        val letDirectory = File(path, storysFolder)
//        val fileStory = File(letDirectory, "$StoryAlbumName.ini")
////        Log.d("Print", "${fileStorys.keys.size}")
////        for (i in fileStorys.keys)
////        {
////            Log.d("Print", "$i=${fileStorys[i]}\n")
////        }
//
//        fileStory.printWriter().use {
//                out ->
//            for (i in fileStorys.keys)
//            {
//                val wrt = "$i$delimiterPhotoAndStory${fileStorys[i]}"
//                out.println(wrt)
//                Log.d("PrintEND", wrt)
//            }
//        }
//        super.onStop()
//    }

}
