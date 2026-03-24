package org.delcom.data


import kotlinx.serialization.Serializable

@Serializable
data class LikeResponse(
    val postId: Int,
    val likeCount: Int,
    val liked: Boolean
)