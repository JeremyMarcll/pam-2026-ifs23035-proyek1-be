package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZoneOffset
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.Clock

import org.delcom.dao.UserDAO
import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.PostDAO
import org.delcom.dao.LikeDAO
import org.delcom.dao.CommentDAO
import org.delcom.entities.User
import org.delcom.entities.RefreshToken
import org.delcom.entities.Post
import org.delcom.entities.Like
import org.delcom.entities.Comment


suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun userDAOToModel(dao: UserDAO) = User(
    dao.id.value.toString(),
    dao.name,
    dao.username,
    dao.password,
    dao.photo,
    dao.bio,
    dao.createdAt,
    dao.updatedAt
)

fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    dao.id.value.toString(),
    dao.userId.toString(),
    dao.refreshToken,
    dao.authToken,
    dao.createdAt,
)

fun postDAOToModel(dao: PostDAO) = Post(
    id = dao.id.value.toString(),
    userId = dao.userId.value.toString(),
    title = dao.title,
    description = dao.description,
    imageUrl = dao.image,
    kategori = dao.kategori,

    createdAt = dao.createdAt,

    updatedAt = dao.updatedAt ?: dao.createdAt
)



fun likeDAOToModel(dao: LikeDAO) = Like(
    id = dao.id.value.toString(),
    postId = dao.postId.value.toString(),
    userId = dao.userId.value.toString()
)

fun commentDAOToModel(dao: CommentDAO) = Comment(
    id = dao.id.value.toString(),
    postId = dao.postId.value.toString(),
    userId = dao.userId.value.toString(),
    content = dao.content,
    createdAt = dao.createdAt
)