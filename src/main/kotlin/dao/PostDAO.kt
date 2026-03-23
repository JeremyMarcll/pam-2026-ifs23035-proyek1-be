package org.delcom.dao

import org.delcom.tables.PostTable
import org.delcom.tables.LikeTable
import org.delcom.tables.CommentTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

/*
    POST DAO
 */
class PostDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, PostDAO>(PostTable)

    var userId by PostTable.userId
    var title by PostTable.title
    var description by PostTable.description
    var image by PostTable.image
    var createdAt by PostTable.createdAt
    var updatedAt by PostTable.updatedAt
}


/*
    LIKE DAO
 */
class LikeDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, LikeDAO>(LikeTable)

    var postId by LikeTable.postId
    var userId by LikeTable.userId
}


/*
    COMMENT DAO
 */
class CommentDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, CommentDAO>(CommentTable)

    var postId by CommentTable.postId
    var userId by CommentTable.userId
    var content by CommentTable.content
    var createdAt by CommentTable.createdAt
    var updatedAt by PostTable.updatedAt
}