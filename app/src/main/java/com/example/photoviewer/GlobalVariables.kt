package com.example.photoviewer

import android.graphics.Bitmap
import android.net.Uri


val delimiterPhotoAndStory = "<`\\=>/=/<\\=\\`=>"
val nNewLine = "`<\\n>/n/<\\n\\n>`"
val delimiter = "/.*:*.\\"

var uris: MutableList<Uri> = mutableListOf()

val dataFolder = "PhotoViewerData/Albums"
val storysFolder = "PhotoViewerData/Storys"
var storyAlbumName = "storys"

val fileStorys = mutableMapOf<String, String>()

var bitMap: MutableList<Bitmap> = mutableListOf()
var images: MutableList<Uri> = mutableListOf() // Uri изображений

var isPortrait = true
var touchBar = false
