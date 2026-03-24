package org.delcom.repositories

import org.delcom.entities.Comment

interface ICommentRepository {

    suspend fun createComment(comment: Comment): Comment

    suspend fun getCommentsByPost(postId: String): List<Comment>

    suspend fun updateComment(id: String, content: String): Boolean

    suspend fun deleteComment(id: String): Boolean

    suspend fun countByPost(postId: String): Int
}