package org.delcom.module

import org.delcom.repositories.*
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.PostService
import org.delcom.services.CommentService
import org.delcom.services.LikeService
import org.koin.dsl.module

fun appModule(jwtSecret: String) = module {

    single<IUserRepository> {
        UserRepository()
    }

    single {
        UserService(get(),get())
    }

    single<IRefreshTokenRepository> {
        RefreshTokenRepository()
    }

    single {
        AuthService(jwtSecret, get(), get())
    }

    single<IPostRepository> {
        PostRepository()
    }

    single {
        PostService(get(), get(), get(), get())
    }

    single<ICommentRepository> {
        CommentRepository()
    }

    single {
        CommentService(get(), get())
    }

    single<ILikeRepository> {
        LikeRepository()
    }

    single {
        LikeService(get(), get())
    }
}