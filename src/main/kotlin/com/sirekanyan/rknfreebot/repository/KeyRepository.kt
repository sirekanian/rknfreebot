package com.sirekanyan.rknfreebot.repository

import com.sirekanyan.rknfreebot.repository.model.Keys
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

interface KeyRepository {
    fun saveKey(location: String, index: Int, content: File)
}

class KeyRepositoryImpl(url: String) : KeyRepository {

    init {
        Database.connect(url)
        transaction {
            SchemaUtils.create(Keys)
        }
    }

    override fun saveKey(location: String, index: Int, content: File) {
        transaction {
            Keys.insert {
                it[Keys.location] = location
                it[Keys.index] = index
                it[Keys.content] = ExposedBlob(content.readBytes())
            }
        }
    }

}