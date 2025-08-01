package com.example.astrataskapp.data.mapper

import com.example.astrataskapp.data.dto.PostDto
import com.example.astrataskapp.domain.model.Post


fun PostDto.toPost(): Post {
    return Post(
        id = id,
        title = title,
        content = content,
        image = photo,
        createdAt = created_at,
        updatedAt = updated_at
    )
}
