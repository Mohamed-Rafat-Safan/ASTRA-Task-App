package com.example.astrataskapp.domain.use_case

import android.net.Uri
import com.example.astrataskapp.domain.model.Post
import com.example.astrataskapp.domain.repository.PostRepository


class PostUseCase(
    private val repository: PostRepository,
) {
    suspend fun getAllPosts(): List<Post> = repository.getAllPosts()

    suspend fun getPostById(postId: Int): Post = repository.getPostById(postId)

    suspend fun addPost(title: String, content: String, imageUri: Uri?): Post {
        return repository.addPost(title, content, imageUri)
    }

    suspend fun updatePost(postId: Int, title: String, content: String, imageUri: Uri?): Post {
        return repository.updatePost(postId, title, content, imageUri)
    }

    suspend fun deletePost(postId: Int): Post {
        return repository.deletePost(postId)
    }
}
