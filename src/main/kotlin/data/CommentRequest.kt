package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Comment

@Serializable
data class CommentRequest(
    var postId: Int = 0,
    var content: String = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postId" to postId,
            "content" to content
        )
    }

    fun toEntity(userId: Int): Comment {
        return Comment(
            postId = postId.toString(),
            userId = userId.toString(),
            content = content
        )
    }
}