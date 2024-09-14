package com.example.photoviewer

import android.graphics.Bitmap
import android.net.Uri


// Надо внести немного ясности в структуру данных файлов
// dataFolder - тут хранятся альбомы фоток в формате uri...hash
// storysFolder - тут хранится альбом {storyAlbumName} с историями в формате hash...story

// Разделители
val delimiterPhotoAndStory = "<`\\=>/=/<\\=\\`=>"
val nNewLine = "`<\\n>/n/<\\n\\n>`"
val delimiterUriAndHash = "/.*:*.\\"

// Вынес в глобаль т.к используются комменты отсюда в FullImageActivity
val fileStorys = mutableMapOf<String, String>()

// Пути до файлов
val dataFolder = "PhotoViewerData/Albums"
val storysFolder = "PhotoViewerData/Storys"
var storyAlbumName = "storys"

// Uri изображений для загрузки нужной картинки там где не используется адаптер + содержит все uri альбома
var images: MutableList<Uri> = mutableListOf() // Uri изображений

// Костыль для нормальной работы AlbumImagesActivity+FullImageActivity+StoryImageActivity
var pageTransition: MutableList<Boolean> = mutableListOf()
