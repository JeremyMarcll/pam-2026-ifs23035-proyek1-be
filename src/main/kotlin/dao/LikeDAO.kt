package org.delcom.dao

import org.delcom.tables.LikeTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class LikeDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, LikeDAO>(LikeTable)

    var postId by LikeTable.postId
    var userId by LikeTable.userId
}