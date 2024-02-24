package com.spycone.next.willbe.file

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.security.MessageDigest

/*
* fun findDuplicateMediaFiles(
    context: Context, progressCallback: (Int) -> Unit, completeCallback: (ArrayList<File>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {


        val mediaFiles = getAllMediaFiles(context, progressCallback)
        val duplicates = ArrayList<File>()
        val hashSet = HashSet<String>()
        val existingFiles = HashMap<String, File>()

        mediaFiles.forEachIndexed { index, file ->
            val md5Hash = calculateHash(file)

            if (hashSet.contains(md5Hash)) {
                duplicates.add(existingFiles[md5Hash]!!)
                duplicates.add(file)
                Log.d(
                    "DuplicateMediaFiles",
                    "Found duplicate file: ${existingFiles[md5Hash]!!.path}     ${file.path} "
                )
            } else {
                hashSet.add(md5Hash)
                existingFiles[md5Hash] = file
            }

            val progress = ((index + 1) * 100) / mediaFiles.size
            Log.d("DuplicateFiles--", "Total -->${index} scanned files")

            progressCallback(progress)
        }

        Log.d("DuplicateMediaFiles", "Found -->${duplicates.size} duplicate files")
        completeCallback(duplicates)
    }
}
* */


@RequiresApi(Build.VERSION_CODES.N)
fun findDuplicateMediaFiles(
    context: Context,
    progressCallback: (Int) -> Unit,
    completeCallback: (ArrayList<File>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val mediaFiles = getAllMediaFiles(context, progressCallback)
        val batchSize = 100 // Process files in batches of 100
        val totalBatches = (mediaFiles.size + batchSize - 1) / batchSize
        val duplicates = ArrayList<File>()
        val hashSet = HashSet<String>()
        val existingFiles = HashMap<String, File>()

        for (batchIndex in 0 until totalBatches) {
            val startIndex = batchIndex * batchSize
            val endIndex = minOf(startIndex + batchSize, mediaFiles.size)

            // Process files in the current batch concurrently
            val batchResults = mediaFiles.subList(startIndex, endIndex).map { file ->
                async {
                    val md5Hash = calculateHash(file)
                    Pair(file, md5Hash)
                }
            }.awaitAll()

            // Update hashSet and existingFiles
            batchResults.forEach { (file, md5Hash) ->
                if (hashSet.contains(md5Hash)) {
                    duplicates.add(existingFiles[md5Hash]!!)
                    duplicates.add(file)

                } else {
                    hashSet.add(md5Hash)
                    existingFiles[md5Hash] = file
                }
            }

            val progress = ((batchIndex + 1) * 100) / totalBatches
            Log.d("DuplicateFiles--", "Processed batch $batchIndex/$totalBatches")
            progressCallback(progress)
        }

        Log.d("DuplicateMediaFiles", "Found -->${duplicates.size} duplicate files")
        completeCallback(duplicates)
    }
}



private fun getAllMediaFiles(context: Context, progressCallback: (Int) -> Unit): List<File> {
    val mediaFiles = ArrayList<File>()

    val uri: Uri = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
    val selection =
        "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE} OR " + "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO} OR " + "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO}"
    val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, null, null)

    cursor?.use {
        val dataIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val totalFiles = it.count
        var processedFiles = 0

        while (it.moveToNext()) {
            val filePath = it.getString(dataIndex)
            val file = File(filePath)
            if (file.isFile) {
                mediaFiles.add(file)

//                processedFiles++
//                val progress = (processedFiles * 100) / totalFiles
//                progressCallback(progress)
            }
        }
    }
    Log.d("DuplicateMediaFiles--->", "Found ${mediaFiles.size} media files")


    return mediaFiles
}


private fun calculateMD5(file: File): String {
    val digest = MessageDigest.getInstance("MD5")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        var bytesRead = input.read(buffer)
        while (bytesRead != -1) {
            digest.update(buffer, 0, bytesRead)
            bytesRead = input.read(buffer)
        }
    }
    val md5Bytes = digest.digest()
    return md5Bytes.joinToString("") { "%02x".format(it) }
}

private fun calculateHash(file: File): String {
    val digest = MessageDigest.getInstance("SHA-1")
    file.inputStream().buffered().use { input ->
        val buffer = ByteArray(8192)
        var bytesRead = input.read(buffer)
        while (bytesRead != -1) {
            digest.update(buffer, 0, bytesRead)
            bytesRead = input.read(buffer)
        }
    }
    val hashBytes = digest.digest()
    return hashBytes.joinToString("") { "%02x".format(it) }
}
