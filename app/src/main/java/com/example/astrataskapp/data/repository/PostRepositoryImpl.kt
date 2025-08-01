package com.example.astrataskapp.data.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.astrataskapp.data.mapper.toPost
import com.example.astrataskapp.data.remote.PostApi
import com.example.astrataskapp.domain.model.Post
import com.example.astrataskapp.domain.repository.PostRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PostRepositoryImpl(
    private val postApi: PostApi,
    private val application: Application,
) : PostRepository {

    override suspend fun getAllPosts(): List<Post> {
        val listPostDto = postApi.getAllPosts()

        return listPostDto.map { postDto ->
            postDto.toPost()
        }
    }

    override suspend fun getPostById(postId: Int): Post {
        return postApi.getPostById(postId).toPost()
    }

    override suspend fun addPost(title: String, content: String, imageUri: Uri?): Post {
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageUri?.let {
            val file = File(getPathFromUri(application, it))
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", file.name, requestBody)
        }

        Log.d("FilePath", "Image Path: ${imageUri}")

        return postApi.addPost(titleBody, contentBody, imagePart).toPost()
    }

    override suspend fun updatePost(
        postId: Int,
        title: String,
        content: String,
        imageUri: Uri?,
    ): Post {
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageUri?.let {
            val file = File(getPathFromUri(application, it))
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", file.name, requestBody)
        }

        return postApi.updatePost(postId, titleBody, contentBody, imagePart).toPost()
    }

    override suspend fun deletePost(postId: Int): Post {
        return postApi.deletePost(postId).toPost()
    }


    private fun getPathFromUri(context: Context, uri: Uri): String {
        var filePath = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val fileName = it.getString(columnIndex)
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, fileName)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                filePath = file.absolutePath
            }
        }
        return filePath
    }


}




