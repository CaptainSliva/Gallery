package com.example.photoviewer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.File


class StoryImageActivity: Activity() {

    val fileStorys = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        val intent = intent
        val pInfo = intent.extras!!.getString("photoName").toString()
        val position = intent.extras!!.getInt("photoID")
        var editFlag = false
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_story)

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
        val letDirectory = File(path, "LET")
        if (!letDirectory.exists()) {

            letDirectory.mkdirs()
        }
        val fileStory = File(letDirectory, "story.ini")


        Log.d("Print", "1")
        imageStory.setImageBitmap(bitMap[position])
        try {
            val fileStoryTrash = fileStory.readLines()
            Log.d("Print", fileStoryTrash.toString())
            fileStoryTrash.forEach { file ->
                Log.d("Print", "2")

                val fname = file.split("=")[0]
                val fstory = file.split("=")[1]
                fileStorys[fname] = fstory
                Log.d("Print", "$fname | $fstory | $pInfo")
                if (fname == pInfo) {
                    textViewStory.setText(fstory)
                }
            }
            Log.d("Print", "3")
        }
        catch (e: Exception) {
            fileStory.canonicalFile.delete()
            Toast.makeText(this, "I/O Exception", Toast.LENGTH_SHORT)
        }
        textViewDate.text = pInfo


        buttonEdit.setOnClickListener {
            editFlag = !editFlag
            if (editFlag) {
                textViewStory.isFocusableInTouchMode = true
                buttonEdit.text = "Сохранить"
            }
            else {
                textViewStory.isFocusableInTouchMode = false
                buttonEdit.text = "Редактировать"
                fileStorys[pInfo] = textViewStory.text.toString()
                Log.d("Print", "$pInfo : ${textViewStory.text}")
//                fileStory.printWriter().use {
//                        out ->
//                    for (i in fileStorys.keys)
//                        out.printf(i+"="+fileStorys[i]+"\n")
//                }
            }
        }
        imageStory.setOnClickListener {
            val i = Intent(
                applicationContext,
                FullImageActivity::class.java
            )

            // passing array index
            i.putExtra("photoID", position)
            i.putExtra("photoPath", pInfo)
            startActivity(i)
        }
    }

    override fun onStop() {
        val path = applicationContext.filesDir
        val letDirectory = File(path, "LET")
        val fileStory = File(letDirectory, "story.ini")
        Log.d("Print", "${fileStorys.keys.size}")
        for (i in fileStorys.keys)
        {
            Log.d("Print", "$i=${fileStorys[i]}\n")
        }

        fileStory.printWriter().use {
                out ->
            for (i in fileStorys.keys)
            {
                out.printf("$i=${fileStorys[i]}\n")
                Log.d("Print", "$i=${fileStorys[i]}\n")
            }
        }
        super.onStop()
    }


}
