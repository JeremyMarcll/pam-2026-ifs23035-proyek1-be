package org.delcom.repositories

import org.delcom.dao.CommentDAO
import org.delcom.entities.Comment
import org.delcom.helpers.commentDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.CommentTable
import org.delcom.tables.PostTable
import org.delcom.tables.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CommentRepository : ICommentRepository {

    override suspend fun createComment(comment: Comment): Comment = suspendTransaction {

        val dao = CommentDAO.new {
            postId = EntityID(UUID.fromString(comment.postId), PostTable)
            userId = EntityID(UUID.fromString(comment.userId), UserTable)
            content = comment.content
        }

        commentDAOToModel(dao)
    }

    override suspend fun getCommentsByPost(postId: String): List<Comment> = suspendTransaction {

        CommentDAO.find {
            CommentTable.postId eq EntityID(UUID.fromString(postId), PostTable)
        }.map { commentDAOToModel(it) }

    }

    override suspend fun updateComment(id: String, content: String): Boolean = suspendTransaction {

        val comment = CommentDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        comment.content = content

        true
    }

    override suspend fun deleteComment(id: String): Boolean = suspendTransaction {

        val comment = CommentDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        comment.delete()

        true
    }

    override suspend fun countByPost(postId: String): Int = suspendTransaction {
        CommentDAO.find { CommentTable.postId eq UUID.fromString(postId) }.count().toInt()
    }
}