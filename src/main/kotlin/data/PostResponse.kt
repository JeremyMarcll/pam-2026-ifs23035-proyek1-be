package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val kategori: String,
    val imageUrl: String?,

    val author: String,

    val totalLikes: Int,
    val totalComments: Int,
    val isLiked: Boolean,

    val createdAt: String
)