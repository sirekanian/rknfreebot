package com.sirekanyan.rknfreebot.repository

import com.sirekanyan.rknfreebot.repository.model.Keys
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

interface KeyRepository {
    fun getLocations(): List<String>
    fun assignKey(location: String, chat: String): Int?
    fun getKey(location: String, index: Int): ByteArray
    fun saveKey(location: String, index: Int, content: File)
}

class KeyRepositoryImpl : KeyRepository {

    init {
        transaction {
            SchemaUtils.create(Keys)
        }
    }

    override fun getLocations(): List<String> =
        transaction {
            Keys.slice(Keys.location).selectAll().withDistinct().map { it[Keys.location] }
        }

    override fun assignKey(location: String, chat: String): Int? =
        transaction {
            val index = findUnassignedIndex(location) ?: return@transaction null
            val updated = assignKeyByIndex(location, index, chat)
            index.takeIf { updated == 1 }
        }

    private fun findUnassignedIndex(location: String): Int? =
        Keys.select { (Keys.location eq location) and (Keys.chat eq null) }.limit(1).singleOrNull()?.get(Keys.index)

    private fun assignKeyByIndex(location: String, index: Int, chat: String): Int =
        Keys.update({ (Keys.location eq location) and (Keys.index eq index) }) { it[Keys.chat] = chat }

    override fun getKey(location: String, index: Int): ByteArray =
        transaction {
            Keys.select { (Keys.location eq location) and (Keys.index eq index) }.single()[Keys.content].bytes
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