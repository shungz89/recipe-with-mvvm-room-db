package com.ftasia.recipeapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ImageUtils {
    companion object {
        fun saveDrawableToInternalStorage(
            context: Context,
            drawableId: Int,
            fileName: String
        ): String? {
            // Get the drawable
            val drawable: Drawable = context.resources.getDrawable(drawableId, null)

            // Convert drawable to bitmap
            val bitmap: Bitmap = if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                // Create a bitmap if drawable is not a BitmapDrawable
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                ).apply {
                    val canvas = Canvas(this)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                }
            }

            // Get the internal storage directory
            val directory = context.filesDir

            // Create the file in internal storage
            val file = File(directory, fileName)
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                fileOutputStream?.close()
            }

            Log.d("Save Image","File saved at ${file.absolutePath}")
            // Return the absolute path of the file
            return file.absolutePath
        }


        fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                return BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
            }
            return null
        }

        // Function to convert Bitmap to Base64 String
        fun bitmapToBase64(bitmap: Bitmap): String {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        // Function to convert Base64 String to Bitmap
        fun base64ToBitmap(base64String: String): Bitmap {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
    }
}

