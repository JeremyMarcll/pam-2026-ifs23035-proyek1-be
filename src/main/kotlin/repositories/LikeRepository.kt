package org.delcom.repositories

import org.delcom.dao.LikeDAO
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.LikeTable
import org.delcom.tables.PostTable
import org.delcom.tables.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

class LikeRepository : ILikeRepository {

    override suspend fun countByPost(postId: String): Int = suspendTransaction {
        LikeDAO.find { LikeTable.postId eq UUID.fromString(postId) }.count().toInt()
    }

    override suspend fun exists(postId: String, userId: String): Boolean = suspendTransaction {
        LikeDAO.find {
            (LikeTable.postId eq UUID.fromString(postId)) and
                    (LikeTable.userId eq UUID.fromString(userId))
        }.empty().not()
    }

    override suspend fun toggle(postId: String, userId: String): Boolean = suspendTransaction {

        val existing = LikeDAO.find {
            (LikeTable.postId eq UUID.fromString(postId)) and
                    (LikeTable.userId eq UUID.fromString(userId))
        }.firstOrNull()

        if (existing != null) {
            existing.delete()
            false // unlike
        } else {
            LikeDAO.new {
                this.postId = EntityID(UUID.fromString(postId), PostTable)
                this.userId = EntityID(UUID.fromString(userId), UserTable)
            }
            true // like
        }
    }

}