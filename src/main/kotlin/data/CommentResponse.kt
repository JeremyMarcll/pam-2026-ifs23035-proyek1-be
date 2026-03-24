package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: String,
    val postId: String,
    val userId: String,
    val author: String,
    val content: String,
    val createdAt: String
)