package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Comment(
    var id: String = UUID.randomUUID().toString(),
    var postId: String,
    var userId: String,
    var content: String,

    @Contextual
    val createdAt: Instant = Clock.System.now()
)