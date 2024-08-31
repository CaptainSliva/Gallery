package com.example.photoviewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.MessageDigest

class FunctionsImages {

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun compressBitmap(bmp: Bitmap, size: Int): Bitmap {
        val stream = ByteArrayOutputStream()
        val dimension = bmp.width.coerceAtMost(bmp.height)
//        val bitmap = ThumbnailUtils.extractThumbnail(bmp, dimension, dimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
//        val resized = Bitmap.createScaledBitmap(ThumbnailUtils.extractThumbnail(bmp, dimension, dimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT), 200, 200, true)
//        resized.compress(Bitmap.CompressFormat.JPEG, 15, stream)
        if (android.os.Build.VERSION.SDK_INT < 30) {
            Bitmap.createScaledBitmap(ThumbnailUtils.extractThumbnail(bmp, dimension, dimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT), size, size, true).compress(
                Bitmap.CompressFormat.WEBP, 100, stream)
        } else {
            Bitmap.createScaledBitmap(ThumbnailUtils.extractThumbnail(bmp, dimension, dimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT), size, size, true).compress(
                Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream)
        }
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }

}