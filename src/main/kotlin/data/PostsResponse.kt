package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class PostsResponse(
    val posts: List<PostResponse>,
    val limit: Int,
    val offset: Long,
    val hasMore: Boolean
)