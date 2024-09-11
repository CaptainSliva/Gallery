package com.example.photoviewer

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.INVISIBLE
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import com.github.chrisbanes.photoview.PhotoView
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


class FullImageActivity: Activity() {

    var albumPath = ""
    var imageName = ""
    var StoryAlbumName = "storys"
    var touchBar = false


    val storysFolder = "PhotoViewerData/Storys"

    override fun onCreate(savedInstanceState: Bundle?) {

//        val w: Window = window

        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_image)

        val intent = intent
        val position = intent.extras!!.getInt("photoID")
        val photoPath = intent.extras!!.getString("photoPath").toString()
        albumPath = intent.extras!!.getString("albumName").toString()
        val path = applicationContext.filesDir
        val letDirectory = File(path, storysFolder)

        val bRotate: Button = findViewById(R.id.idRotateButton)
        val bStory: Button = findViewById(R.id.idStoryButton)
        val vTouchView: View = findViewById(R.id.idContainer)
        val bDelete: Button = findViewById(R.id.idDeleteButton)
        val bShare: Button = findViewById(R.id.idShareButton)
        val textStory: TextView = findViewById(R.id.idTextStory)
        val menu: ConstraintLayout = findViewById(R.id.idSecondContainer)
        val photoView: PhotoView = findViewById(R.id.full_image_view)
        var f = false


        photoView.setImageBitmap(
            MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                images[position]
            )
        )
        val margins: MarginLayoutParams = photoView.layoutParams as ViewGroup.MarginLayoutParams
        textStory.text = FunctionsFiles().readStory(applicationContext, File(letDirectory, "$StoryAlbumName.ini"), photoPath, fileStorys)

        bRotate.setOnClickListener {
            requestedOrientation = if (isPortrait) {
                f = true
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            } else {
                f = false
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            }
            isPortrait = !isPortrait

            if (f) {
                margins.setMargins(0, 300, 0, 0)
                photoView.layoutParams = margins
            } else {
                margins.setMargins(0, 0, 300, 0)
                photoView.layoutParams = margins
            }
        }

        bStory.setOnClickListener {
            Log.d("PrintF", photoPath)
            val i = Intent(
                applicationContext,
                StoryImageActivity::class.java
            )
            if (pageTransition[0]) pageTransition[1] = true

            // passing array index
            i.putExtra("photoID", position)
            i.putExtra("photoPath", photoPath)
            i.putExtra("albumName", albumPath)
            startActivity(i)
            finish()
        }

        bDelete.setOnClickListener {
            deleteDialog(
                getString(R.string.title_image_delete),
                getString(R.string.messge_image_delete),
                photoPath,
                albumPath,
                position
            )
            pageTransition[2] = true
        }

        bShare.setOnClickListener {
            print(photoPath)
            print(photoPath.toUri())
//            val sendIntent = Intent()
//            sendIntent.setAction(Intent.ACTION_SEND)
//            sendIntent.putExtra(Intent.EXTRA_STREAM, photoPath.toUri())
//            sendIntent.setType("image/jpeg")
//            startActivity(sendIntent)
        }

        photoView.setOnClickListener {

            if (touchBar) {
                vTouchView.visibility = INVISIBLE
                menu.visibility = INVISIBLE
            } else {
                vTouchView.visibility = VISIBLE
                menu.visibility = VISIBLE
            }
            touchBar = !touchBar
        }

    }

    fun deleteDialog(title: String, message: String, imageDeleteName: String, albumPath: String, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage("$message?")
        imageName = FunctionsFiles().albumName(imageDeleteName)
        builder.setPositiveButton(
            "ОК"
        ) { dialog, which -> imageName = imageDeleteName
            FunctionsFiles().deletePhoto(this, albumPath, imageDeleteName)
            val i = Intent(applicationContext, AlbumImagesActivity::class.java)
            i.putExtra("removePosition", position)
            images.removeAt(position)
            bitMap.removeAt(position)
            finish()
        }

        builder.setNegativeButton(
            "ОТМЕНА"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }



}