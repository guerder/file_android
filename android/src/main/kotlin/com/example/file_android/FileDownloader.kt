package com.example.file_android

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.FileNotFoundException
import java.io.IOException


class FileDownloader {
    fun saveBase64File(context: Context, call: MethodCall, result: MethodChannel.Result) {
        val dados = call.arguments as List<String>

        val nomeArquivo = dados[0]
        val mimeType = dados[2]

        try {
            val contentResolver = context.contentResolver
            val fileUri: Uri?
            if (mimeType.contains("image")) {
                saveBase64ImageFile(context, contentResolver, dados, result)
            } else if (mimeType.contains("video")) {
                saveBase64VideoFile(context, contentResolver, dados, result)
            } else {
                saveBase64DocumentFile(context, contentResolver, dados, result)
            }
        } catch (e1: IOException) {
            result.error("IOE1", e1.message, "")
        } catch (e2: FileNotFoundException) {
            result.error("IOE2", e2.message, "Arquivo não encontrado: $nomeArquivo")
        } catch (e3: Exception) {
            result.error("IOE3", e3.message, "")
        }
    }

    private fun saveBase64ImageFile(context: Context, contentResolver: ContentResolver, dados: List<String>, result: MethodChannel.Result) {
        val nomeArquivo = dados[0]
        val subDiretorio = dados[1]
        val mimeType = dados[2]
        val b64 = dados[3]

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nomeArquivo)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!subDiretorio.isNullOrEmpty()) {
                    val pathDiv = if (subDiretorio.startsWith("/")) "" else "/"
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + pathDiv + subDiretorio)
                }
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val collection = MediaStore.Images.Media.getContentUri("external")

        val fileUri = contentResolver.insert(collection, contentValues)
        if (fileUri != null) {
            val file = contentResolver.openFileDescriptor(fileUri, "w")
            if (file == null) {
                result.error("404", "File not found", "")
            } else {
                val os = ParcelFileDescriptor.AutoCloseOutputStream(file)
                os.write(Base64.decode(b64, Base64.DEFAULT))
                os.close()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(fileUri, contentValues, null, null)
            }

//            result.success(fileUri.path)
            result.success(getPath(context, fileUri))
        }
    }

    private fun saveBase64VideoFile(context: Context, contentResolver: ContentResolver, dados: List<String>, result: MethodChannel.Result) {
        val nomeArquivo = dados[0]
        val subDiretorio = dados[1]
        val mimeType = dados[2]
        val b64 = dados[3]

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, nomeArquivo)
            put(MediaStore.Video.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!subDiretorio.isNullOrEmpty()) {
                    val pathDiv = if (subDiretorio.startsWith("/")) "" else "/"
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + pathDiv + subDiretorio)
                }
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val collection = MediaStore.Video.Media.getContentUri("external")
        val fileUri = contentResolver.insert(collection, contentValues)
        if (fileUri != null) {
            val file = contentResolver.openFileDescriptor(fileUri, "w")
            if (file == null) {
                result.error("404", "File not found", "")
            } else {
                val os = ParcelFileDescriptor.AutoCloseOutputStream(file)
                os.write(Base64.decode(b64, Base64.DEFAULT))
                os.close()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(fileUri, contentValues, null, null)
            }

//            result.success(fileUri.path)
            result.success(getPath(context, fileUri))
        }
    }

    private fun saveBase64DocumentFile(context: Context, contentResolver: ContentResolver, dados: List<String>, result: MethodChannel.Result) {
        val nomeArquivo = dados[0]
        val subDiretorio = dados[1]
        val mimeType = dados[2]
        val b64 = dados[3]

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, nomeArquivo)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!subDiretorio.isNullOrEmpty()) {
                    val pathDiv = if (subDiretorio.startsWith("/")) "" else "/"
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + pathDiv + subDiretorio)
                }
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val collection = MediaStore.Files.getContentUri("external")
        val fileUri = contentResolver.insert(collection, contentValues)
        if (fileUri != null) {
            val file = contentResolver.openFileDescriptor(fileUri, "w")
            if (file == null) {
                result.error("404", "File not found", "")
            } else {
                val os = ParcelFileDescriptor.AutoCloseOutputStream(file)
                os.write(Base64.decode(b64, Base64.DEFAULT))
                os.close()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(fileUri, contentValues, null, null)
            }

//            result.success(fileUri.path)
            result.success(getPath(context, fileUri))
        }
    }

    fun getPath(context: Context, uri: Uri): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String?>(
                        split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String?>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
                column
        )
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }
}