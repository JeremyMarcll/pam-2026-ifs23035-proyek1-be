package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Comment
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ICommentRepository
import org.delcom.repositories.IUserRepository

class CommentService(
    private val commentRepository: ICommentRepository,
    private val userRepository: IUserRepository
) {

    // ===== CREATE =====
    suspend fun post(call: ApplicationCall) {

        // 🔐 Ambil user dari token
        val user = ServiceHelper.getAuthUser(call, userRepository)

        // 📥 Ambil request body
        val request = call.receive<Map<String, String>>()

        // ✅ Validasi
        val validator = ValidatorHelper(request)
        validator.required("postId", "Post ID tidak boleh kosong")
        validator.required("content", "Komentar tidak boleh kosong")
        validator.validate()

        // 🧱 Buat entity
        val comment = Comment(
            userId = user.id,
            postId = request["postId"]!!,
            content = request["content"]!!
        )

        // 💾 Simpan ke database
        val result = commentRepository.createComment(comment)

        // 🎯 Mapping ke response (INI YANG PENTING)
        val response = CommentResponse(
            id = result.id,
            postId = result.postId,
            userId = result.userId,
            author = user.username, // 🔥 nama user
            content = result.content,
            createdAt = result.createdAt.toString()
        )

        // 📤 Kirim ke frontend
        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menambah data",
                data = mapOf("comment" to response)
            )
        )
    }

    // ===== GET BY POST =====
    suspend fun getByPost(call: ApplicationCall) {

        val postId = call.parameters["postId"]
            ?: throw AppException(400, "Post ID tidak valid")

        val comments = commentRepository.getCommentsByPost(postId)

        val response = comments.map { comment ->

            val user = userRepository.getById(comment.userId)

            CommentResponse(
                id = comment.id,
                postId = comment.postId,
                userId = comment.userId,
                author = user?.username ?: "Unknown", // 🔥 penting
                content = comment.content,
                createdAt = comment.createdAt.toString()
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data",
                data = mapOf("comments" to response)
            )
        )
    }

    // ===== UPDATE =====
    suspend fun put(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val request = call.receive<Map<String, String>>()

        val validator = ValidatorHelper(request)
        validator.required("content", "Komentar tidak boleh kosong")
        validator.validate()

        // ⚠️ idealnya cek owner di repo
        val isUpdated = commentRepository.updateComment(
            id,
            request["content"]!!
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal update komentar")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengubah data",
                data = mapOf("message" to "Komentar berhasil diupdate")
            )
        )
    }

    // ===== DELETE =====
    suspend fun delete(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val isDeleted = commentRepository.deleteComment(id)

        if (!isDeleted) {
            throw AppException(400, "Gagal hapus komentar")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menghapus data",
                data = mapOf("message" to "Komentar berhasil dihapus")
            )
        )
    }
}