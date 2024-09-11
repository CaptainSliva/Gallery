package com.example.photoviewer


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    val numberOfColumns = 2
    val GALLERY_REQUEST: Int = 1
    var albumName = ""
    val REQUEST_CODE_PERMISSIONS = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        itemModel = "album"
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_CODE_PERMISSIONS
                )
            }

        }

        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }


        changeColorStatusbar(R.color.violet, this, this.window)
        isDarkTheme(resources, Configuration(), findViewById<ConstraintLayout>(R.id.main), this)


        val albumRV: RecyclerView = findViewById(R.id.idAlbumsView)
        val newAlbum: ImageButton = findViewById(R.id.idNewAlbumBtn)
        val buttonActions: ImageButton = findViewById(R.id.idActionsBtn)
        //val itemDecorator = SpacingItemDecorator(12) //TODO думать надо
        //albumRV.addItemDecoration(itemDecorator)
        popupMenu(buttonActions)


//        TODO удалить лишнее (закомментил т.к вроде эти строки мне все истории потёрли(()
//        FunctionsFiles().checkImages(this@MainActivity)
//        FunctionsFiles().checkStorys(this@MainActivity)

        val tabLayout: TabLayout = findViewById(R.id.tabView)
        tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                print("${tab} ${tab.id} ${tab.text} ${tab.tag}")
                Tost(applicationContext, "${tab.text} is selected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        var albumList = ArrayList<RecyclerData>()
        val albums = ArrayList<String>()
        FunctionsFiles().getAlbumsFiles(applicationContext).forEach { album -> albums.add(album) }

//        val imagesAlbums = listOf("AlbumsPreView/Vagner.png")
        val imagesAlbums = listOf("AlbumsPreView/rydra.jpg")

        var bitMap = ArrayList<Bitmap>()

        for (i in imagesAlbums.indices) {
            try {
                //these images are stored in the root of "assets"
                bitMap += getBitmapFromAsset(imagesAlbums[i])
                Log.d("BITA", i.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        for (i in albums.indices) {
            albumList += RecyclerData(FunctionsFiles().albumName(albums[i]), bitMap[0])
        }


        // Запуск окна с изображениями
        val adapter = RecyclerViewAdapter(albumList, this)
        val layoutManager = GridLayoutManager(this, numberOfColumns)
        albumRV.layoutManager = layoutManager
        albumRV.adapter = adapter

        albumRV.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                albumRV,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        Toast.makeText(
                            applicationContext, "Album ${albumList[position].getTitle()} selected",
                            Toast.LENGTH_SHORT
                        ).show()

                        val i = Intent(
                            applicationContext,
                            AlbumImagesActivity::class.java
                        )
                        i.putExtra("albumPath", albums[position])
                        startActivity(i)
                    }

                    override fun onLongClick(view: View, position: Int) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                        builder
                            .setTitle(getString(R.string.choose_action_with_album))
                            .setPositiveButton("") { dialog, which ->
                                // Do something.
                            }
                            .setNegativeButton("") { dialog, which ->
                                // Do something else.
                            }
                            .setItems(arrayOf(getString(R.string.choose_action_delete_album), getString(R.string.choose_action_rename_album))) { dialog, which ->
                                if (which == 0) {
                                    FunctionsApp().deleteAlbumDialog( this@MainActivity,
                                        getString(R.string.title_album_delete), getString(R.string.messge_album_delete), albums[position],
                                        albumName, albumList, position, adapter
                                    )
                                }
                                if (which == 1) {
                                    renameAlbumDialog(
                                        getString(R.string.title_rename_album),
                                        getString(R.string.message_rename_album),
                                        albums[position],
                                        albumName
                                    )
                                }
                            }

                        val dialog: AlertDialog = builder.create()
                        dialog.show()

                    }
                })
        )
//        albumRV.onItemClickListener = AdapterView.OnItemClickListener { u, t, position, r ->
//            Toast.makeText(
//                applicationContext, albumList[position].courseName + " selected",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            val i = Intent(
//                applicationContext,
//                AlbumImagesActivity::class.java
//            )
//            i.putExtra("albumPath", albums[position])
//            startActivity(i)
//
////            val tst = Intent(applicationContext, ImageTest::class.java)
////            tst.putExtra("albumPath", albums[position])
////            startActivity(tst)
//        }
//
//        // Удаление альбома
//        albumRV.onItemLongClickListener = OnItemLongClickListener { parent, view, index, id ->
//
//            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//            builder
//                .setTitle(getString(R.string.choose_action_with_album))
//                .setPositiveButton("") { dialog, which ->
//                    // Do something.
//                }
//                .setNegativeButton("") { dialog, which ->
//                    // Do something else.
//                }
//                .setItems(arrayOf(getString(R.string.choose_action_delete_album), getString(R.string.choose_action_rename_album))) { dialog, which ->
//                    if (which == 0) {
//                        dialog(
//                            getString(R.string.title_album_delete),
//                            getString(R.string.messge_album_delete),
//                            albums[index]
//                        )
//                    }
//                    if (which == 1) {
//                        renameAlbumDialog(
//                            getString(R.string.title_rename_album),
//                            getString(R.string.message_rename_album),
//                            albums[index]
//                        )
//                    }
//                }
//
//            val dialog: AlertDialog = builder.create()
//            dialog.show()
//
//
//
//            true
//        }


        newAlbum.setOnClickListener {
//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.setType("image/*")
//            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
            createAlbumDialog(getString(R.string.title_new_album), getString(R.string.message_new_album))
        }


    }

    private fun popupMenu(popupBtn: ImageButton) {
        // creating a object of Popupmenu
        val popupMenu = PopupMenu(this, popupBtn)

        // we need to inflate the object
        // with popup_menu.xml file
        popupMenu.inflate(R.menu.popup_menu)

        // adding click listener to image
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_on_comment -> {
                    Toast.makeText(this, "Поиск по комментарию", Toast.LENGTH_SHORT).show()
                    val i = Intent(
                        applicationContext,
                        SearchOnComment::class.java
                    )
                    startActivity(i)
                    true
                }
                R.id.export_comments -> {
                    val sendIntent = Intent(Intent.ACTION_SEND)
                    val fileStorysPath = File(applicationContext.filesDir, "$storysFolder/$storyAlbumName.ini")
                    val a = fileStorysPath.toUri()
                    val b = a.scheme
                    print("$a\n$b")
//                    sendIntent.setAction(Intent.ACTION_SEND)
//                    sendIntent.putExtra(Intent.EXTRA_STREAM, fileStorysPath.toUri()) // content://com.android.providers.media.documents/document/image%3A1000000089
//                    sendIntent.setType("image/jpeg")
//                    startActivity(sendIntent)
//                    val uri = FileProvider.getUriForFile(this, "$this.provider", fileStorysPath)
//                    sendIntent.setType("application/ini")
//                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(("file:/$fileStorysPath")))
//                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
//                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
//                    startActivity(Intent.createChooser(sendIntent, "Share File"))

                    true
                }
                R.id.import_comments -> {
                    Toast.makeText(this, "☻☻☻", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    true
                }
            }

        }

        // event on press on image
        popupBtn.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu,true)
            }
            catch (e: Exception)
            {
                Log.d("error", e.toString())
            }
            finally {
                popupMenu.show()
            }
            true
        }

    }


    override fun onResume() {
        super.onResume()
        itemModel = "album"
    }

    @Throws(IOException::class)
    private fun getBitmapFromAsset(strName: String): Bitmap {
        val assetManager = assets

        val istr = assetManager.open(strName)
        val bitmap = BitmapFactory.decodeStream(istr)
        istr.close()

        return bitmap
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.popup_menu, menu)
        return true
    }

    fun renameAlbumDialog(title: String, message: String, albumRenameName: String, albumName: String) {
        var albumName = albumName
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage("$message \"${FunctionsFiles().albumName(albumRenameName)}\"?")
        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        builder.setView(input)
        builder.setPositiveButton(
            "ОК"
        ) { dialog, which -> albumName = input.text.toString()
            if (albumName.isNotEmpty()) {
                if (albumName.contains('/') || albumName.contains('\\') || albumName.contains('.')) {
                    Toast.makeText(applicationContext, "Название альбома не должно содержать символы: \".\", \"\\\", \"/\"", Toast.LENGTH_LONG).show()
                }
                else {
                    FunctionsFiles().renameAlbum(this, albumRenameName, input.text.toString())
                    val i = Intent(
                        applicationContext,
                        MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }
        }

        builder.setNegativeButton(
            "ОТМЕНА"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    //Создание альбома
    fun createAlbum(albumName: String) {
        val path = applicationContext.filesDir
        val letDirectory = File(path, dataFolder)
        if (!letDirectory.exists()) {
            letDirectory.mkdir()
        }
        letDirectory.listFiles()?.forEach { i ->
            if (FunctionsFiles().albumName(i.toString()) == albumName) {
                Toast.makeText(this, "Альбом с таким названием уже создан", Toast.LENGTH_LONG).show()
                return
            }
        }
        File(letDirectory, "$albumName.ini").appendText("")
        this.albumName = albumName

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(intent, 1)
    }

    fun createAlbumDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        builder.setView(input)
        builder.setPositiveButton(
            "ОК"
        ) { dialog, which -> albumName = input.text.toString()
            if (albumName.isNotEmpty()) {
                if (albumName.contains('/') || albumName.contains('\\') || albumName.contains('.')) {
                    Toast.makeText(this, "Название альбома не должно содержать символы: \".\", \"\\\", \"/\"", Toast.LENGTH_LONG).show()
                }
                else {
                    createAlbum(albumName)
                }
            }
        }

        builder.setNegativeButton(
            "ОТМЕНА"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
//        TODO("Я так понимаю, что надо формировать или список или битмап выбранных объектов. И список не список, а файл с путями до фоток, что бы потом их подгружать при открытии альбома")

        if (resultCode == GALLERY_REQUEST) {
            var fileList = ArrayList<String>()
            val selectedImages = resultData!!.clipData
            for (i in 0..<selectedImages!!.itemCount) {
                val path = selectedImages?.getItemAt(i)?.uri.toString()
                fileList.add(path)
                Log.d("PrintAddPh", path)

            }
            FunctionsFiles().addPhotoToAlbumFile(applicationContext, albumName, fileList)


//                val i = Intent(
//                    applicationContext,
//                    ListOfFilenames::class.java
//                )
//
//                i.putExtra("file", fileList)
//                startActivity(i)
            val i = Intent(
                applicationContext,
                AlbumImagesActivity::class.java
            )
            i.putExtra("albumPath", albumName)
            startActivity(i)
        }


        var fileList = ArrayList<String>()

        if (requestCode == 1) {


            if (resultData?.data != null) {
                Log.i("Path:", resultData.toString())
                contentResolver.takePersistableUriPermission(
                    resultData.data!!,
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                )
                fileList.add(resultData.data.toString())
            }

            if (resultData?.clipData != null) {
                val selectedImages = resultData!!.clipData
                Log.i("Path:", resultData.toString())
                for (i in 0..<selectedImages!!.itemCount) {
                    val path = selectedImages?.getItemAt(i)?.uri.toString()
                    fileList.add(path)
                    Log.d("PrintAddPh", path)
                    contentResolver.takePersistableUriPermission(
                        path.toUri(),
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    )

                }

            }
        }
        if (fileList().isNotEmpty()) {
            Log.d("PrintNoEmpty", "Is not Empty")
            FunctionsFiles().addPhotoToAlbumFile(applicationContext, albumName, fileList)
            val i = Intent(
                applicationContext,
                MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }




}

