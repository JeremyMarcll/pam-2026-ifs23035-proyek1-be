package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.PostService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val authService: AuthService by inject()
    val userService: UserService by inject()
    val postService: PostService by inject()

    install(StatusPages) {

        exception<AppException> { call, cause ->

            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        exception<Throwable> { call, cause ->

            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {

        get("/") {
            call.respondText("API telah berjalan. Dibuat oleh Jeremy Manullang.")
        }

        /*
        ==============================
        AUTH
        ==============================
         */
        route("/auth") {

            post("/register") {
                authService.postRegister(call)
            }

            post("/login") {
                authService.postLogin(call)
            }

            post("/refresh") {
                authService.postRefreshToken(call)
            }

            post("/logout") {
                authService.postLogout(call)
            }
        }

        /*
        ==============================
        AUTHENTICATED ROUTES
        ==============================
         */
        authenticate(JWTConstants.NAME) {

            /*
            ==============================
            USER
            ==============================
             */
            route("/users") {

                get("/me") {
                    userService.getMe(call)
                }

                put("/me") {
                    userService.putMe(call)
                }

                put("/me/password") {
                    userService.putMyPassword(call)
                }

                put("/me/photo") {
                    userService.putMyPhoto(call)
                }

                get("/{id}/photo") {
                    userService.getPhoto(call)
                }
            }

            /*
            ==============================
            POSTS
            ==============================
             */
            route("/posts") {

                get {
                    postService.getAll(call)
                }

                get("/{id}") {
                    postService.getById(call)
                }

                post {
                    postService.createPost(call)
                }

                put("/{id}") {
                    postService.updatePost(call)
                }

                delete("/{id}") {
                    postService.delete(call)
                }

                get("/search") {
                    postService.search(call)
                }

                /*
                ==============================
                LIKE
                ==============================
                 */
                post("/{postId}/likes") {
                    postService.toggleLike(call)
                }

                /*
                ==============================
                COMMENT
                ==============================
                 */
                get("/{postId}/comments") {
                    postService.getComments(call)
                }

                post("/{postId}/comments") {
                    postService.createComment(call)
                }
            }

            /*
            ==============================
            COMMENT MANAGEMENT
            ==============================
             */
            route("/comments") {

                put("/{id}") {
                    postService.updateComment(call)
                }

                delete("/{id}") {
                    postService.deleteComment(call)
                }
            }
        }
    }
}