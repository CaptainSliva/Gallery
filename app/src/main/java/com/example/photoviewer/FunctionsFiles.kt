package com.example.photoviewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import java.io.File


class FunctionsFiles: AppCompatActivity() {

    fun deletePhoto(context: Context, albumName: String, imageName: String) {
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val letDirectoryStor = File(path, storysFolder)
        val fileAlbum = File(letDirectory, "${albumName(albumName)}.ini")
        val content = File(letDirectory, "${albumName(albumName)}.ini").readLines()
        val storyDirectoryWrite = File(letDirectoryStor, "$storyAlbumName.ini")
        val storysContent = File(path, "$storysFolder/$storyAlbumName.ini").readLines()

        var hash = ""
        var imageCheck: HashMap<String, Int> = checkImages((context))

        fileAlbum.printWriter().use {
                out ->
            for (line in content)
            {
                if (line.split(delimiterUriAndHash)[0] != imageName) {
                    out.println(line)
                }
                else {
                    hash = line.split(delimiterUriAndHash)[1]
                }
            }
        }
        storyDirectoryWrite.printWriter().use {
                out ->
            for (line in storysContent)
            {
                print("${imageCheck[line.split(delimiterPhotoAndStory)[0]]!! > 1} (${imageCheck[line.split(delimiterPhotoAndStory)[0]]}) ${line.split(delimiterPhotoAndStory)[0] == hash}")

                if (line.split(delimiterPhotoAndStory)[0] != hash) out.println(line)
                else{
                    if(imageCheck[line.split(delimiterPhotoAndStory)[0]]!! > 1 && line.split(delimiterPhotoAndStory)[0] in hash) out.println(line)
                }
            }
        }
    }

    fun albumName(name: String): String
    {
        return name.split('/')[name.split("/").size - 1].split('.')[0]
    }

    fun addPhotoToAlbumFile(context: Context, albumName: String, images: ArrayList<String>) {
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val fileAlbum = File(letDirectory, "${albumName(albumName)}.ini")
        val fileRead = fileAlbum.readLines()
        var content: ArrayList<String> = ArrayList()
        fileRead.forEach { line ->
            content.add(FunctionsImages().md5(line.split(delimiterUriAndHash)[1]))
        }

        fileAlbum.printWriter().use {
                out ->
            if (images.size > 1) {
                for (i in images.indices)
                {
                    if (FunctionsImages().md5(images[i]) !in content) {
                        out.println("${images[i]}${delimiterUriAndHash}${FunctionsImages().md5(images[i])}")
                    }
                }
            }

            else {
                try {
                    if (FunctionsImages().md5(images[0]) !in content) {
                        out.println("${images[0]}${delimiterUriAndHash}${FunctionsImages().md5(images[0])}")
                    }
                }
                catch (e: Exception) {
                }
            }

            fileRead.forEach { line ->
                if (line.split(delimiterUriAndHash)[0] !in images) {
                    out.println("$line")
                }
            }
        }
    }

    fun getHashesPhotosFromAlbum(context: Context, albumName: String): ArrayList<String> {
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val fileAlbum = File(letDirectory, "${albumName(albumName)}.ini")
        var content: ArrayList<String> = ArrayList()
        fileAlbum.readLines().forEach { line ->
            content.add(line.split(delimiterUriAndHash)[1])
        }
        return content
    }

    fun readStory(context: Context, fileStory: File, photoPath: String, fileStorys: MutableMap<String, String>): String {
        var theStory = ""
        try {
            val fileStoryTrash = fileStory.readLines()
            fileStoryTrash.forEach { file ->

                val fname = file.split(delimiterPhotoAndStory)[0]
                val fstory = file.split(delimiterPhotoAndStory)[1]
                fileStorys[fname] = fstory
                if (fname == FunctionsImages().md5(photoPath)) {
                    theStory = fstory.replace(nNewLine, "\n")
                }
            }
        }
        catch (e: Exception) {
            Toast.makeText(context, "I/O Exception", Toast.LENGTH_LONG).show()
        }
        return theStory
    }

    fun findImagesOnHash(context: Context, allHashs: MutableList<String>): MutableList<Uri> {
        //TODO Найти картинку по хэшу md5(yourUri.toString())
        var hashList: MutableList<String> = mutableListOf()
        var uris: MutableList<Uri> = mutableListOf()
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val fileStorysContent = letDirectory.listFiles()
        fileStorysContent.forEach { i ->
            i.readLines().forEach{ line ->
                val hash = line.split(delimiterUriAndHash)[1]
                if (hash in allHashs && hash !in hashList) {
                    uris.add(line.split(delimiterUriAndHash)[0].toUri())
                    hashList.add(hash)
                }
            }
        }
        return uris
    }

    fun findImageAlbumOnHash(context: Context, allHashs: MutableList<String>): MutableList<String> {
        var hashList: MutableList<String> = mutableListOf()
        var albums: MutableList<String> = mutableListOf()
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val fileStorysContent = letDirectory.listFiles()
        fileStorysContent.forEach { i ->
            i.readLines().forEach{ line ->
                val hash = line.split(delimiterUriAndHash)[1]
                if (hash in allHashs && hash !in hashList) {
                    albums.add(i.toString())
                    hashList.add(hash)
                }
            }
        }
        return albums
    }

    fun deleteAlbum(context: Context, album: String){
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val letDirectoryStor = File(path, storysFolder)
        val storyDirectoryWrite = File(letDirectoryStor, "$storyAlbumName.ini")
        val storysContent = File(path, "$storysFolder/$storyAlbumName.ini").readLines()
        var hashes: MutableList<String> = mutableListOf()
        var imageCheck: HashMap<String, Int> = checkImages((context))

        if (!letDirectory.exists()) {
            letDirectory.mkdirs()
        }
        val albums = letDirectory.listFiles()


        albums?.forEach { el ->
            val file = el.toString()
            if (file == album) {
                val content = el.readLines()
                content.forEach { line ->
                    hashes.add(line.split(delimiterUriAndHash)[1])
                }
                el.delete()
            }
        }
        storyDirectoryWrite.printWriter().use {
                out ->
            for (line in storysContent)
            {
                if (line.split(delimiterPhotoAndStory)[0] !in hashes) out.println(line)

                else{
                    if(imageCheck[line.split(delimiterPhotoAndStory)[0]]!! > 1 && line.split(delimiterPhotoAndStory)[0] in hashes) out.println(line)
                }
            }
        }
    }

    fun renameAlbum(context: Context, albumForRenameName: String, albumName: String) {
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        var currentAlbumPath = ""
        var listAlbums: ArrayList<String> = ArrayList()
        letDirectory.listFiles()?.forEach { i ->
            listAlbums.add(FunctionsFiles().albumName(i.toString()))
            if (FunctionsFiles().albumName(i.toString()) == albumName.toString()) {
                Toast.makeText(context, "Альбом с таким названием уже создан", Toast.LENGTH_LONG).show()
                return
            }
            else {
                val dot = i.toString().substring(0, i.toString().lastIndexOf('.'))
                currentAlbumPath = "${dot.substring(0, dot.lastIndexOf('/'))}"
            }
        }
        File(albumForRenameName).renameTo(File("$currentAlbumPath/$albumName.ini"))
    }

    fun getAlbumsFiles(context: Context): ArrayList<String> {
        var albums = ArrayList<String>()
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        if (!letDirectory.exists()) {
            letDirectory.mkdirs()
        }
        letDirectory.listFiles()?.forEach { f ->
            val file = f.toString()
            albums.add(file)
        }
        Log.d("PrintGet", albums.toString())
        return albums
    }

    fun checkImages(context: Context): HashMap<String, Int> {
        //TODO Найти картинку по хэшу md5(yourUri.toString())
        var hashes: HashMap<String, Int> = hashMapOf()
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
            letDirectory.listFiles().forEach { i ->
                print(i.readLines())
                i.readLines().forEach{ line ->
                    val hash = line.split(delimiterUriAndHash)[1]
                    hashes[hash] = hashes.getOrDefault(hash, 0) + 1
                }
        }
        print(hashes)
        return hashes
    }

    fun deleteUnUseStorys(context: Context, photohash: String) {
        //TODO Найти картинку по хэшу md5(yourUri.toString())
        var lines: MutableList<String> = mutableListOf()
        val path = context.filesDir
        val fileStorys = File(path, "$storysFolder/$storyAlbumName.ini")
        print(fileStorys.readLines())
        fileStorys.readLines().forEach{ line ->
            if (photohash != line.split(delimiterPhotoAndStory)[0]) {
                lines.add(line)
            }
        }
        fileStorys.printWriter().use { out ->
            lines.forEach { story ->
                out.println(story)
            }
        }
        print("del", lines)

    }

    fun deleteUnUseImages(context: Context, albumName: String, photohash: String) {
        //TODO Найти картинку по хэшу md5(yourUri.toString())
        var lines: MutableList<String> = mutableListOf()
        val path = context.filesDir
        val letDirectory = File(path, dataFolder)
        val fileAlbum = File(letDirectory, "${albumName(albumName)}.ini")
        print(fileAlbum.readLines())
        fileAlbum.readLines().forEach{ line ->
            if (photohash != line.split(delimiterUriAndHash)[1]) {
                lines.add(line)
            }
        }
        fileAlbum.printWriter().use { out ->
            lines.forEach { image ->
                out.println(image)
            }
        }
        print("del", lines)

    }

}