package com.sirekanyan.rknfreebot.extensions

import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import java.net.URI
import java.util.*

fun DefaultAbsSender.sendText(chatId: Long, text: String): Message =
    execute(SendMessage(chatId.toString(), text))

fun DefaultAbsSender.sendCatPhoto(chatId: Long, title: String) {
    val path = if (title.isEmpty()) "/cat" else "/cat/says/${title.replace('/', ' ')}"
    val query = "filter=sepia&width=500&size=50&type=square&uuid=${UUID.randomUUID()}"
    val uri = URI("https", "cataas.com", path, query, null)
    execute(SendPhoto(chatId.toString(), InputFile(uri.toASCIIString())))
}
