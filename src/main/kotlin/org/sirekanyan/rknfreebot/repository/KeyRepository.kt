package com.sirekanyan.rknfreebot.repository

import com.sirekanyan.rknfreebot.repository.model.Keys
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

interface KeyRepository {
    fun getLocations(): List<String>
    fun ejectKey(location: String): ByteArray?
    fun saveKey(location: String, index: Int, content: File)
    fun getCounts(): Map<String, Long>
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

    override fun ejectKey(location: String): ByteArray? =
        transaction {
            val key = findKeyByLocation(location) ?: return@transaction null
            val index = key[Keys.index]
            val content = key[Keys.content]
            Keys.deleteWhere { (Keys.location eq location) and (Keys.index eq index) }
            content.bytes
        }

    private fun findKeyByLocation(location: String): ResultRow? =
        Keys.select { Keys.location eq location }.limit(1).singleOrNull()

    override fun saveKey(location: String, index: Int, content: File) {
        transaction {
            Keys.insert {
                it[Keys.location] = location
                it[Keys.index] = index
                it[Keys.content] = ExposedBlob(content.readBytes())
            }
        }
    }

    override fun getCounts(): Map<String, Long> =
        transaction {
            Keys.slice(Keys.location, Keys.location.count()).selectAll().groupBy(Keys.location).associate {
                it[Keys.location] to it[Keys.location.count()]
            }
        }

}