package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object PostTable : UUIDTable("posts") {

    val userId = reference(
        "user_id",
        UserTable,
        onDelete = ReferenceOption.CASCADE
    )

    val title = varchar("title", 200)

    val description = text("description")

    val image = text("image").nullable()

    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}