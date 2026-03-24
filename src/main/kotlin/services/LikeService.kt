package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.helpers.ServiceHelper
import org.delcom.repositories.ILikeRepository
import org.delcom.repositories.IUserRepository

class LikeService(
    private val likeRepository: ILikeRepository,
    private val userRepository: IUserRepository
) {

    // ===== TOGGLE LIKE =====
    suspend fun toggle(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val postId = call.parameters["postId"]
            ?: throw AppException(400, "Post ID tidak valid")

        val isLiked = likeRepository.toggle(
            user.id,
            postId
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil",
                data = mapOf(
                    "liked" to isLiked
                )
            )
        )
    }

    // ===== GET LIKE COUNT =====
    suspend fun getLikes(call: ApplicationCall) {

        val postId = call.parameters["postId"]
            ?: throw AppException(400, "Post ID tidak valid")

        val total = likeRepository.countByPost(postId)

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data",
                data = mapOf(
                    "totalLikes" to total
                )
            )
        )
    }
}