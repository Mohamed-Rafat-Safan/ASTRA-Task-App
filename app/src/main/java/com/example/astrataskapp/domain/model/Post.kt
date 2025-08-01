package com.example.astrataskapp.domain.model

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val image: String,
    val createdAt: String,
    val updatedAt: String,
)
