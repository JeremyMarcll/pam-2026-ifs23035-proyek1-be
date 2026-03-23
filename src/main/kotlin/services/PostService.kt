package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IPostRepository
import org.delcom.repositories.IUserRepository
import java.util.UUID
import org.delcom.data.AppException
import org.delcom.data.DataResponse



class PostService(
    private val postRepository: IPostRepository,
    private val userRepository: IUserRepository
) {

    // ===============================
    // CREATE POST
    // ===============================
    suspend fun createPost(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val request = call.receive<PostRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.validate()

        val post = postRepository.createPost(
            request.toEntity(UUID.fromString(user.id)) // UUID aman
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Post berhasil dibuat",
                data = mapOf("post" to post)
            )
        )
    }

    // ===============================
    // GET ALL
    // ===============================
    suspend fun getAll(call: ApplicationCall) {

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val posts = postRepository.getPosts(limit, offset)

        val response = posts.map {

            val likeCount = postRepository.countLikes(it.id)
            val commentCount = postRepository.countComments(it.id)

            PostResponse(
                id = it.id,
                title = it.title,
                image = it.image,
                description = it.description,
                username = it.userId,
                likeCount = likeCount.toInt(),
                commentCount = commentCount.toInt(),
                createdAt = it.createdAt.toString()
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data post",
                data = mapOf(
                    "posts" to response,
                    "limit" to limit,
                    "offset" to offset,
                    "hasMore" to (posts.size == limit)
                )
            )
        )
    }

    // ===============================
    // GET BY ID
    // ===============================
    suspend fun getById(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val post = postRepository.getPostById(id)
            ?: throw AppException(404, "Post tidak ditemukan")

        val likeCount = postRepository.countLikes(post.id)
        val commentCount = postRepository.countComments(post.id)

        val response = PostResponse(
            id = post.id,
            title = post.title,
            image = post.image,
            description = post.description,
            username = post.userId,
            likeCount = likeCount.toInt(),
            commentCount = commentCount.toInt(),
            createdAt = post.createdAt.toString()
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil detail post",
                data = mapOf("post" to response)
            )
        )
    }

    // ===============================
    // UPDATE
    // ===============================
    suspend fun updatePost(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val request = call.receive<PostRequest>()

        val updated = postRepository.updatePost(
            id,
            request.title,
            request.description,
            request.image
        )

        if (!updated) {
            throw AppException(400, "Gagal update post")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Post berhasil diupdate",
                data = null
            )
        )
    }

    // ===============================
    // DELETE
    // ===============================
    suspend fun delete(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val deleted = postRepository.deletePost(id)

        if (!deleted) {
            throw AppException(400, "Gagal delete post")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Post berhasil dihapus",
                data = null
            )
        )
    }

    // ===============================
    // SEARCH
    // ===============================
    suspend fun search(call: ApplicationCall) {

        val keyword = call.request.queryParameters["q"]
        val sort = call.request.queryParameters["sort"]

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val posts = postRepository.searchPosts(keyword, sort, limit, offset)

        val response = posts.map {

            val likeCount = postRepository.countLikes(it.id)
            val commentCount = postRepository.countComments(it.id)

            PostResponse(
                id = it.id,
                title = it.title,
                image = it.image,
                description = it.description,
                username = it.userId,
                likeCount = likeCount.toInt(),
                commentCount = commentCount.toInt(),
                createdAt = it.createdAt.toString()
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mencari post",
                data = mapOf("posts" to response)
            )
        )
    }

    // ===============================
    // TOGGLE LIKE
    // ===============================
    suspend fun toggleLike(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val postId = call.parameters["postId"]
            ?: throw AppException(400, "Post ID tidak valid")

        val liked = postRepository.toggleLike(user.id, postId)

        val likeCount = postRepository.countLikes(postId)

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil toggle like",
                data = LikeResponse(
                    postId = postId,
                    likeCount = likeCount.toInt(),
                    liked = liked
                )
            )
        )
    }

    // ===============================
    // CREATE COMMENT
    // ===============================
    suspend fun createComment(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val request = call.receive<CommentRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("content", "Komentar tidak boleh kosong")
        validator.validate()

        val comment = postRepository.createComment(
            request.toEntity(UUID.fromString(user.id))
        )

        val response = CommentResponse(
            id = comment.id,
            postId = comment.postId,
            username = comment.userId,
            content = comment.content,
            createdAt = comment.createdAt.toString()
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Komentar berhasil dibuat",
                data = mapOf("comment" to response)
            )
        )
    }

    // ===============================
// UPDATE COMMENT
// ===============================
    suspend fun updateComment(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "Comment ID tidak valid")

        val request = call.receive<CommentRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("content", "Komentar tidak boleh kosong")
        validator.validate()

        val updated = postRepository.updateComment(
            id,
            request.content
        )

        if (!updated) {
            throw AppException(404, "Komentar tidak ditemukan")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Komentar berhasil diupdate",
                data = null
            )
        )
    }

    // ===============================
    // GET COMMENTS
    // ===============================
    suspend fun getComments(call: ApplicationCall) {

        val postId = call.parameters["postId"]
            ?: throw AppException(400, "Post ID tidak valid")

        val comments = postRepository.getCommentsByPost(postId)

        val response = comments.map {
            CommentResponse(
                id = it.id,
                postId = it.postId,
                username = it.userId,
                content = it.content,
                createdAt = it.createdAt.toString()
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil komentar",
                data = mapOf("comments" to response)
            )
        )
    }

    // ===============================
// DELETE COMMENT
// ===============================
    suspend fun deleteComment(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "Comment ID tidak valid")

        val deleted = postRepository.deleteComment(id)

        if (!deleted) {
            throw AppException(404, "Komentar tidak ditemukan")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Komentar berhasil dihapus",
                data = null
            )
        )
    }
}