package com.example.astrataskapp.domain.repository

import android.net.Uri
import com.example.astrataskapp.domain.model.Post

interface PostRepository {
    suspend fun getAllPosts(): List<Post>

    suspend fun getPostById(postId: Int): Post

    suspend fun addPost(title: String, content: String, imageUri: Uri?): Post

    suspend fun updatePost(postId: Int, title: String, content: String, imageUri: Uri?): Post

    suspend fun deletePost(postId: Int): Post

}