package com.sirekanyan.rknfreebot.repository

import com.sirekanyan.rknfreebot.repository.model.Keys
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

interface KeyRepository {
    fun getLocations(): List<String>
    fun getKey(location: String): ByteArray
    fun saveKey(location: String, index: Int, content: File)
}

class KeyRepositoryImpl(url: String) : KeyRepository {

    init {
        Database.connect(url)
        transaction {
            SchemaUtils.create(Keys)
        }
    }

    override fun getLocations(): List<String> =
        transaction {
            Keys.slice(Keys.location).selectAll().withDistinct().map { it[Keys.location] }
        }

    override fun getKey(location: String): ByteArray =
        transaction {
            Keys.select { Keys.location eq location }.first()[Keys.content].bytes
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