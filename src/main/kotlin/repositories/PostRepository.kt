package org.delcom.repositories

import org.delcom.dao.PostDAO
import org.delcom.entities.Post
import org.delcom.helpers.postDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.PostTable
import org.delcom.tables.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.or
import java.util.UUID

class PostRepository : IPostRepository {

    override suspend fun createPost(post: Post): Post = suspendTransaction {

        val dao = PostDAO.new {

            userId = EntityID(UUID.fromString(post.userId), UserTable)
            title = post.title
            description = post.description
            kategori = post.kategori
            image = post.imageUrl
            createdAt = post.createdAt
            updatedAt = post.updatedAt

        }

        postDAOToModel(dao)
    }

    override suspend fun getPostById(id: String): Post? = suspendTransaction {

        PostDAO.findById(UUID.fromString(id))
            ?.let { postDAOToModel(it) }

    }

    override suspend fun getPosts(limit: Int, offset: Long): List<Post> = suspendTransaction {

        PostDAO.all()
            .limit(limit)
            .offset(offset)
            .map { postDAOToModel(it) }

    }

    override suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        kategori: String,
        image: String?
    ): Boolean = suspendTransaction {

        val post = PostDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        post.title = title
        post.description = description
        post.kategori = kategori
        post.image = image

        true
    }

    override suspend fun deletePost(id: String): Boolean = suspendTransaction {

        val post = PostDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        post.delete()

        true
    }

    override suspend fun searchPosts(
        keyword: String?,
        sort: String,
        limit: Int,
        offset: Long
    ): List<Post> = suspendTransaction {

        val baseQuery = if (!keyword.isNullOrEmpty()) {
            PostDAO.find {
                (PostTable.title like "%$keyword%") or
                        (PostTable.description like "%$keyword%")
            }
        } else {
            PostDAO.all()
        }

        val sorted = when (sort) {
            "likes" -> baseQuery.sortedByDescending { it.id.value } // sementara dummy
            else -> baseQuery.sortedByDescending { it.createdAt }
        }

        sorted
            .drop(offset.toInt())
            .take(limit)
            .map { postDAOToModel(it) }
    }

    override suspend fun getPostsByUserId(userId: String): List<Post> = suspendTransaction {
        PostDAO
            .find { PostTable.userId eq UUID.fromString(userId) }
            .map(::postDAOToModel)
    }

}