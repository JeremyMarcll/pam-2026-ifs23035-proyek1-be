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
import org.delcom.services.CommentService
import org.delcom.services.LikeService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val authService: AuthService by inject()
    val userService: UserService by inject()
    val postService: PostService by inject()
    val commentService: CommentService by inject()
    val likeService: LikeService by inject()

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
                status = HttpStatusCode.fromValue(500),
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
            call.respondText("Dibuat oleh Bunga Rhiza Sitorus | Delcom Post")
        }

        /**
         * AUTH ROUTES
         */
        route("/auth") {

            post("/login") {
                authService.postLogin(call)
            }

            post("/register") {
                authService.postRegister(call)
            }

            post("/refresh-token") {
                authService.postRefreshToken(call)
            }

            post("/logout") {
                authService.postLogout(call)
            }
        }

        authenticate(JWTConstants.NAME) {

            /**
             * USER ROUTES
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

                get("/me/posts") {
                    postService.getMyPosts(call)
                }
            }

            /**
             * POST ROUTES (FIXED)
             */
            route("/posts") {

                get {
                    postService.getAll(call) // ✅ /posts
                }

                get("/feed") {
                    postService.getFeed(call)
                }

                get("/search") {
                    postService.search(call)
                }

                post {
                    postService.post(call)
                }

                get("/{id}") {
                    postService.getById(call)
                }

                put("/{id}") {
                    postService.put(call)
                }

                delete("/{id}") {
                    postService.delete(call)
                }
            }

            /**
             * COMMENT ROUTES (FIXED)
             */
            route("/comments") {

                get("/post/{postId}") {
                    commentService.getByPost(call)
                }

                post {
                    commentService.post(call)
                }

                put("/{id}") {
                    commentService.put(call)
                }

                delete("/{id}") {
                    commentService.delete(call)
                }
            }

            /**
             * LIKE ROUTES (FIXED & CLEAN)
             */
            route("/posts/{postId}/likes") {

                post {
                    likeService.toggle(call) // toggle like
                }

                get {
                    likeService.getLikes(call) // count like
                }
            }
        }

        /**
         * IMAGE ROUTES
         */
        route("/images") {

            get("/users/{id}") {
                userService.getPhoto(call)
            }

            get("/posts/{id}") {
                postService.getImage(call)
            }
        }
    }
}