package org.sirekanyan.rknfreebot.repository.model

import org.jetbrains.exposed.sql.Table

object Chats : Table() {

    val id = long("id")

    override val primaryKey = PrimaryKey(id)

}