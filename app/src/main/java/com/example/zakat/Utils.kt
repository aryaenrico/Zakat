package com.example.zakat

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "yyyy-MM-dd"
private  val FILE_NAME_FORMAT_2 ="dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

val timeStamp2: String = SimpleDateFormat(
    FILE_NAME_FORMAT_2,
    Locale.US
).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp2, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver

    // membuat file temporary untuk menampung gambar
    val myFile = createTempFile(context)

    // mengubah gambar pada uri
    // menulis data dari uri kedalam file
    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1000)
    var len: Int

    // menulis apa yang ada pada stream (uri temporary file ) dan diletakan pada file
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()
    val result = reduceFileImage(myFile)

    // untuk mengirim data ke server
    return result
}
fun uriToFile2( uri: Uri,context: Context): File? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val contentResolver: ContentResolver = context.contentResolver
    val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val filePath = it.getString(columnIndex)
            return File(filePath)
        }
    }
    return null
}

fun resizeImage(context: Context, imageUri: Uri, targetWidth: Int, targetHeight: Int): Bitmap {
    val contentResolver: ContentResolver = context.contentResolver

    // Open an InputStream from the Uri
    val inputStream: InputStream = contentResolver.openInputStream(imageUri)
        ?: throw IllegalArgumentException("Could not open InputStream for the provided Uri")

    // First, decode the image InputStream using BitmapFactory
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(inputStream, null, options)

    // Calculate the scaling factor to resize the image
    val scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight, targetWidth, targetHeight)

    // Reset the input stream and load and resize the image using BitmapFactory with the calculated scaling factor
    inputStream.reset()
    options.inJustDecodeBounds = false
    options.inSampleSize = scaleFactor
    val resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options)

    return resizedBitmap !!
}

fun calculateScaleFactor(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int): Int {
    val widthScale = srcWidth.toFloat() / dstWidth
    val heightScale = srcHeight.toFloat() / dstHeight
    val scaleFactor = Math.min(widthScale, heightScale)
    return Math.round(scaleFactor)
}



private fun calculateSampleSize(options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int): Int {
    var sampleSize = 1
    val imageWidth = options.outWidth
    val imageHeight = options.outHeight

    if (imageWidth > maxWidth || imageHeight > maxHeight) {
        val halfWidth = imageWidth / 2
        val halfHeight = imageHeight / 2

        while (halfWidth / sampleSize >= maxWidth && halfHeight / sampleSize >= maxHeight) {
            sampleSize *= 2
        }
    }

    return sampleSize
}

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

// agar kamera tidka flip 90 derajat ke kanan apa kekiri saat perubahan configurasi kamera
fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}
fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)

    var compressQuality = 50
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 3000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

