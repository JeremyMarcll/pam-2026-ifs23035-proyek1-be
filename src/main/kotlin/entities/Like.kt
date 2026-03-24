package org.delcom.entities

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Like(
    var id: String = UUID.randomUUID().toString(),
    var postId: String,
    var userId: String
)