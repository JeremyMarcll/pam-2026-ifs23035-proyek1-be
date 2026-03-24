package org.delcom.dao

import org.delcom.tables.PostTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class PostDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, PostDAO>(PostTable)

    var userId by PostTable.userId

    var title by PostTable.title

    var description by PostTable.description

    var image by PostTable.image

    var kategori by PostTable.kategori

    var createdAt by PostTable.createdAt

    var updatedAt by PostTable.updatedAt
}