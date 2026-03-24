package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CommentTable : UUIDTable("comments") {

    val postId = reference(
        "post_id",
        PostTable,
        onDelete = ReferenceOption.CASCADE
    )

    val userId = reference(
        "user_id",
        UserTable,
        onDelete = ReferenceOption.CASCADE
    )

    val content = text("content")

    val createdAt = timestamp("created_at")
}