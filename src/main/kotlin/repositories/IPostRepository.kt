package org.delcom.repositories

import org.delcom.entities.Post

interface IPostRepository {

    suspend fun createPost(post: Post): Post

    suspend fun getPostById(id: String): Post?

    suspend fun getPosts(limit: Int, offset: Long): List<Post>

    suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        kategori: String,
        image: String?
    ): Boolean

    suspend fun deletePost(id: String): Boolean

    suspend fun searchPosts(
        keyword: String?,
        sort: String,
        limit: Int,
        offset: Long
    ): List<Post>

    suspend fun getPostsByUserId(userId: String): List<Post>
}