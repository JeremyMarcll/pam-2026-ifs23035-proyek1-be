package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.User

@Serializable
data class AuthRequest(
    var name: String = "",
    var username: String = "",
    var password: String = "",
    var newPassword: String = "",
    var bio: String? = null,
    var photo: String? = null
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "username" to username,
            "password" to password,
            "bio" to bio,
            "profileImage" to photo
        )
    }

    fun toEntity(): User {
        return User(
            name = name,
            username = username,
            password = password,
            photo = photo,
            bio = bio,
            updatedAt = Clock.System.now()
        )
    }

}
