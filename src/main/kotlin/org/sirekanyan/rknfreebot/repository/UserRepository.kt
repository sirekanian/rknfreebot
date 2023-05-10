package org.sirekanyan.rknfreebot.repository

import org.sirekanyan.rknfreebot.repository.model.Chats
import org.sirekanyan.rknfreebot.repository.model.Invites
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {
    fun hasChat(id: Long): Boolean
    fun addChat(id: Long)
    fun hasInvite(id: String): Boolean
    fun addInvite(id: String)
}

class UserRepositoryImpl : UserRepository {

    init {
        transaction {
            SchemaUtils.create(Chats)
            SchemaUtils.create(Invites)
        }
    }

    override fun hasChat(id: Long): Boolean =
        transaction {
            Chats.select { Chats.id eq id }.count() > 0
        }

    override fun addChat(id: Long) {
        transaction {
            Chats.insertIgnore { it[Chats.id] = id }
        }
    }

    override fun hasInvite(id: String): Boolean =
        transaction {
            Invites.select { Invites.id eq id }.count() > 0
        }

    override fun addInvite(id: String) {
        transaction {
            Invites.insertIgnore { it[Invites.id] = id }
        }
    }

}