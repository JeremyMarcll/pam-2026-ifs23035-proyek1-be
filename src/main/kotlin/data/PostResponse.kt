package org.delcom.data

import kotlinx.serialization.Serializable

/*
    RESPONSE UNTUK POST
 */
@Serializable
data class PostResponse(
    val id: String,
    val title: String,
    val image: String?,
    val description: String,
    val username: String,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String
)


/*
    RESPONSE UNTUK COMMENT
 */
@Serializable
data class CommentResponse(
    val id: String,
    val postId: String,
    val username: String,
    val content: String,
    val createdAt: String
)


/*
    RESPONSE UNTUK LIKE
 */
@Serializable
data class LikeResponse(
    val postId: String,
    val likeCount: Int,
    val liked: Boolean
)