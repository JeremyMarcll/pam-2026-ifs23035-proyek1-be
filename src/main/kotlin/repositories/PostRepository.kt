package org.delcom.repositories

import org.delcom.dao.*
import org.delcom.entities.Comment
import org.delcom.entities.Post
import org.delcom.helpers.*
import org.delcom.tables.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.util.*

class PostRepository : IPostRepository {

    /*
        POST
     */

    override suspend fun createPost(post: Post): Post = suspendTransaction {

        val dao = PostDAO.new {

            userId = EntityID(UUID.fromString(post.userId), UserTable)
            title = post.title
            description = post.description
            image = post.image

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
        image: String?
    ): Boolean = suspendTransaction {

        val post = PostDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        post.title = title
        post.description = description
        post.image = image

        true
    }

    override suspend fun deletePost(id: String): Boolean = suspendTransaction {

        val post = PostDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        post.delete()

        true
    }


    /*
        SEARCH / SORT / FILTER
     */

    override suspend fun searchPosts(
        keyword: String?,
        sort: String?,
        limit: Int,
        offset: Long
    ): List<Post> = suspendTransaction {

        val query = if (!keyword.isNullOrEmpty()) {

            PostDAO.find {
                (PostTable.title like "%$keyword%") or
                        (PostTable.description like "%$keyword%")
            }

        } else {
            PostDAO.all()
        }

        val sorted = when (sort) {

            "oldest" -> query.sortedBy { it.createdAt }

            "title" -> query.sortedBy { it.title }

            else -> query.sortedByDescending { it.createdAt } // latest default

        }

        sorted
            .drop(offset.toInt())
            .take(limit)
            .map { postDAOToModel(it) }

    }


    /*
        COMMENT
     */

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

    // ===============================
// UPDATE COMMENT
// ===============================
    override suspend fun updateComment(id: String, content: String): Boolean =
        suspendTransaction {

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

    override suspend fun countComments(postId: String): Long = suspendTransaction {

        CommentDAO.find {
            CommentTable.postId eq EntityID(UUID.fromString(postId), PostTable)
        }.count()

    }


    /*
        LIKE
     */

    override suspend fun toggleLike(userId: String, postId: String): Boolean = suspendTransaction {

        val existing = LikeDAO.find {

            (LikeTable.userId eq EntityID(UUID.fromString(userId), UserTable)) and
                    (LikeTable.postId eq EntityID(UUID.fromString(postId), PostTable))

        }.firstOrNull()

        if (existing != null) {

            existing.delete()
            false

        } else {

            LikeDAO.new {

                this.userId = EntityID(UUID.fromString(userId), UserTable)
                this.postId = EntityID(UUID.fromString(postId), PostTable)

            }

            true
        }

    }

    override suspend fun countLikes(postId: String): Long = suspendTransaction {

        LikeDAO.find {
            LikeTable.postId eq EntityID(UUID.fromString(postId), PostTable)
        }.count()

    }

}