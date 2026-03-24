package org.delcom.dao

import org.delcom.tables.CommentTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CommentDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, CommentDAO>(CommentTable)

    var postId by CommentTable.postId
    var userId by CommentTable.userId
    var content by CommentTable.content
    var createdAt by CommentTable.createdAt
}