package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID


/*
    ENTITY POST
 */
@Serializable
data class Post(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var title: String,
    var description: String,
    var image: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),

    @Contextual
    var updatedAt: Instant = Clock.System.now()
)


/*
    ENTITY LIKE
 */
@Serializable
data class Like(
    var id: String = UUID.randomUUID().toString(),
    var postId: String,
    var userId: String
)


/*
    ENTITY COMMENT
 */
@Serializable
data class Comment(
    var id: String = UUID.randomUUID().toString(),
    var postId: String,
    var userId: String,
    var content: String,

    @Contextual
    var updatedAt: Instant = Clock.System.now(),

    @Contextual
    val createdAt: Instant = Clock.System.now()


)