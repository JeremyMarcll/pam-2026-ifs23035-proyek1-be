package org.delcom.repositories

import org.delcom.entities.Post
import org.delcom.entities.Comment

interface IPostRepository {

    /*
        POST
     */

    suspend fun createPost(post: Post): Post

    suspend fun getPostById(id: String): Post?

    suspend fun getPosts(limit: Int, offset: Long): List<Post>

    suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        image: String?
    ): Boolean

    suspend fun deletePost(id: String): Boolean


    /*
        SEARCH / FILTER / SORT
     */

    suspend fun searchPosts(
        keyword: String?,
        sort: String?,
        limit: Int,
        offset: Long
    ): List<Post>


    /*
        COMMENT
     */

    suspend fun createComment(comment: Comment): Comment

    suspend fun getCommentsByPost(postId: String): List<Comment>

    suspend fun updateComment(id: String, content: String): Boolean

    suspend fun deleteComment(id: String): Boolean

    suspend fun countComments(postId: String): Long


    /*
        LIKE
     */

    suspend fun toggleLike(userId: String, postId: String): Boolean

    suspend fun countLikes(postId: String): Long

}