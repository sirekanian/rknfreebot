package org.sirekanyan.rknfreebot.repository.model

import org.jetbrains.exposed.sql.Table

object Invites : Table() {

    val id = text("id")

    override val primaryKey = PrimaryKey(id)

}