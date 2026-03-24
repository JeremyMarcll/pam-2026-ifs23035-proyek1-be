package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class LikeRequest(
    val postId: Int
)