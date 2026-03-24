package org.delcom.repositories

interface ILikeRepository {

    suspend fun countByPost(postId: String): Int

    suspend fun exists(postId: String, userId: String): Boolean

    suspend fun toggle(postId: String, userId: String): Boolean
}