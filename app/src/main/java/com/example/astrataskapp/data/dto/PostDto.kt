package com.example.astrataskapp.data.dto

data class PostDto(
    val content: String,
    val created_at: String,
    val id: Int,
    val photo: String,
    val title: String,
    val updated_at: String,
)