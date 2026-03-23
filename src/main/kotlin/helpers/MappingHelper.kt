package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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


/*
    HELPER UNTUK TRANSACTION SUSPEND
 */
suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)


/*
    USER DAO -> ENTITY
 */
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


/*
    REFRESH TOKEN DAO -> ENTITY
 */
fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    dao.id.value.toString(),
    dao.userId.toString(),
    dao.refreshToken,
    dao.authToken,
    dao.createdAt
)


/*
    POST DAO -> ENTITY
 */
fun postDAOToModel(dao: PostDAO) = Post(
    id = dao.id.value.toString(),
    userId = dao.userId.value.toString(),
    title = dao.title,
    description = dao.description,
    image = dao.image,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)


/*
    LIKE DAO -> ENTITY
 */
fun likeDAOToModel(dao: LikeDAO) = Like(
    id = dao.id.value.toString(),
    postId = dao.postId.value.toString(),
    userId = dao.userId.value.toString()
)


/*
    COMMENT DAO -> ENTITY
 */
fun commentDAOToModel(dao: CommentDAO) = Comment(
    id = dao.id.value.toString(),
    postId = dao.postId.value.toString(),
    userId = dao.userId.value.toString(),
    content = dao.content,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)