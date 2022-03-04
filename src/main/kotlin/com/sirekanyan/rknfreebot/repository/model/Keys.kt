package com.sirekanyan.rknfreebot.repository.model

import org.jetbrains.exposed.sql.Table

object Keys : Table() {

    val location = text("location")
    val index = integer("index")
    val content = blob("content")
    val chat = text("chat").nullable()

    override val primaryKey = PrimaryKey(location, index)

}