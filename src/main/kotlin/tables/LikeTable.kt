package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object LikeTable : UUIDTable("likes") {

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
}