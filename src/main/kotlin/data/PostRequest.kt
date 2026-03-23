package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Post
import org.delcom.entities.Comment
import java.util.UUID

/*
    REQUEST UNTUK MEMBUAT POST
 */
@Serializable
data class PostRequest(
    var title: String = "",
    var image: String? = null,
    var description: String = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "image" to image,
            "description" to description
        )
    }

    fun toEntity(userId: UUID): Post {
        return Post(
            userId = userId.toString(),
            title = title,
            description = description,
            image = image,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}


/*
    REQUEST UNTUK LIKE POST
 */
@Serializable
data class LikeRequest(
    val postId: String
)


/*
    REQUEST UNTUK COMMENT POST
 */
@Serializable
data class CommentRequest(
    var postId: String,
    var content: String = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postId" to postId,
            "content" to content
        )
    }

    fun toEntity(userId: UUID): Comment {
        return Comment(
            postId = postId,
            userId = userId.toString(),
            content = content,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()

        )
    }
}