package com.example.astrataskapp.data.remote

import com.example.astrataskapp.data.dto.PostDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PostApi {

    @GET("blogs")
    suspend fun getAllPosts(): List<PostDto>

    @GET("blogs/show/{postId}")
    suspend fun getPostById(@Path("postId") postId: Int): PostDto

    @Multipart
    @POST("blogs/store")
    suspend fun addPost(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part?
    ): PostDto

    @Multipart
    @POST("blogs/update/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Int,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part?
    ): PostDto

    @POST("blogs/delete/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int): PostDto
}
