package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Post
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IPostRepository
import org.delcom.repositories.IUserRepository
import org.delcom.repositories.ICommentRepository
import org.delcom.repositories.ILikeRepository

class PostService(
    private val postRepository: IPostRepository,
    private val userRepository: IUserRepository,
    private val likeRepository: ILikeRepository,
    private val commentRepository: ICommentRepository
) {

    // ===== LOGIC LAMA (TIDAK DIUBAH) =====
    suspend fun createPost(
        userId: String,
        title: String,
        description: String,
        kategori: String,
        image: String?
    ): Post {
        val post = Post(
            userId = userId,
            title = title,
            description = description,
            kategori = kategori,
            imageUrl = image
        )
        return postRepository.createPost(post)
    }

    suspend fun getPosts(limit: Int, offset: Long): List<Post> {
        return postRepository.getPosts(limit, offset)
    }

    suspend fun getPost(id: String): Post? {
        return postRepository.getPostById(id)
    }

    suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        kategori: String,
        image: String?
    ): Boolean {
        return postRepository.updatePost(id, title, description, kategori, image)
    }

    suspend fun deletePost(id: String): Boolean {
        return postRepository.deletePost(id)
    }

    // ===== HANDLER (SESUAI REFERENSI) =====

    suspend fun getAll(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val posts = postRepository.getPosts(limit, offset)

        val postsWithMeta = posts.map { post ->

            val totalLikes = likeRepository.countByPost(post.id)
            val totalComments = commentRepository.countByPost(post.id)
            val isLiked = likeRepository.exists(post.id, user.id)

            val author = userRepository.getById(post.userId)?.username ?: "Unknown"

            PostResponse(
                id = post.id,
                userId = post.userId,
                title = post.title,
                description = post.description,
                kategori = post.kategori,
                imageUrl = post.imageUrl,
                author = author,
                totalLikes = totalLikes,
                totalComments = totalComments,
                isLiked = isLiked,
                createdAt = post.createdAt.toString()
            )
        }

        val hasMore = posts.size == limit

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data",
                data = PostsResponse(
                    posts = postsWithMeta,
                    limit = limit,
                    offset = offset,
                    hasMore = hasMore
                )
            )
        )
    }

    suspend fun getById(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val post = postRepository.getPostById(id)
            ?: throw AppException(404, "Post tidak ditemukan")

        val totalLikes = likeRepository.countByPost(post.id)
        val totalComments = commentRepository.countByPost(post.id)
        val isLiked = likeRepository.exists(post.id, user.id)

        val author = userRepository.getById(post.userId)?.username ?: "Unknown"

        val response = PostResponse(
            id = post.id,
            userId = post.userId,
            title = post.title,
            description = post.description,
            kategori = post.kategori,
            imageUrl = post.imageUrl,
            author = author,
            totalLikes = totalLikes,
            totalComments = totalComments,
            isLiked = isLiked,
            createdAt = post.createdAt.toString()
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data",
                data = mapOf("post" to response)
            )
        )
    }

    suspend fun post(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepository)

        val request = call.receive<PostRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.validate()

        val post = createPost(
            user.id,
            request.title,
            request.description,
            request.kategori,
            request.imageUrl
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menambah data",
                data = mapOf("post" to post)
            )
        )
    }

    suspend fun put(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val request = call.receive<PostRequest>()

        val isUpdated = updatePost(
            id,
            request.title,
            request.description,
            request.kategori,
            request.imageUrl
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal update post")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengubah data",
                data = mapOf("message" to "Post berhasil diupdate")
            )
        )
    }

    suspend fun delete(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val isDeleted = deletePost(id)

        if (!isDeleted) {
            throw AppException(400, "Gagal delete post")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menghapus data",
                data = mapOf("message" to "Post berhasil dihapus")
            )
        )
    }

    suspend fun getFeed(call: ApplicationCall) {
        getAll(call) // 🔥 reuse aja
    }

    suspend fun search(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val keyword = call.request.queryParameters["q"]
        val sort = call.request.queryParameters["sort"] ?: "latest"

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val posts = postRepository.searchPosts(keyword, sort, limit, offset)

        val postsWithMeta = posts.map { post ->

            val totalLikes = likeRepository.countByPost(post.id)
            val totalComments = commentRepository.countByPost(post.id)
            val isLiked = likeRepository.exists(post.id, user.id)

            val author = userRepository.getById(post.userId)?.username ?: "Unknown"

            PostResponse(
                id = post.id,
                userId = post.userId,
                title = post.title,
                description = post.description,
                kategori = post.kategori,
                imageUrl = post.imageUrl,
                author = author,
                totalLikes = totalLikes,
                totalComments = totalComments,
                isLiked = isLiked,
                createdAt = post.createdAt.toString()
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mencari data",
                data = mapOf("posts" to postsWithMeta)
            )
        )
    }

    suspend fun getImage(call: ApplicationCall) {
        call.respond("image not implemented")
    }

    suspend fun getMyPosts(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val posts = postRepository.getPostsByUserId(user.id)

        val responseList = posts.map { post ->

            val totalLikes = likeRepository.countByPost(post.id)
            val totalComments = commentRepository.countByPost(post.id)
            val isLiked = likeRepository.exists(post.id, user.id)

            val author = userRepository.getById(post.userId)?.username ?: "Unknown"

            PostResponse(
                id = post.id,
                userId = post.userId,
                title = post.title,
                description = post.description,
                kategori = post.kategori,
                imageUrl = post.imageUrl,
                author = author,
                totalLikes = totalLikes,
                totalComments = totalComments,
                isLiked = isLiked,
                createdAt = post.createdAt.toString()
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil my posts",
                data = mapOf("posts" to responseList)
            )
        )
    }
}