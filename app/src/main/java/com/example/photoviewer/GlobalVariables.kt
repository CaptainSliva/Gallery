package com.example.photoviewer

import android.graphics.Bitmap
import android.net.Uri


// Надо внести немного ясности в структуру данных файлов
// dataFolder - тут хранятся альбомы фоток в формате uri...hash
// storysFolder - тут хранится альбом {storyAlbumName} с историями в формате hash...story

val delimiterPhotoAndStory = "<`\\=>/=/<\\=\\`=>"
val nNewLine = "`<\\n>/n/<\\n\\n>`"
val delimiterUriAndHash = "/.*:*.\\"

var uris: MutableList<Uri> = mutableListOf()

val dataFolder = "PhotoViewerData/Albums"
val storysFolder = "PhotoViewerData/Storys"
var storyAlbumName = "storys"

val fileStorys = mutableMapOf<String, String>()

var bitMap: MutableList<Bitmap> = mutableListOf()
var images: MutableList<Uri> = mutableListOf() // Uri изображений

var isPortrait = true

var pageTransition: MutableList<Boolean> = mutableListOf()
