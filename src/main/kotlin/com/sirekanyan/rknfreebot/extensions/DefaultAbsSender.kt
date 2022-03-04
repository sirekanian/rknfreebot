package com.sirekanyan.rknfreebot.extensions

import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.File
import java.net.URI
import java.util.*

fun DefaultAbsSender.sendText(chatId: Long, text: String): Message =
    execute(SendMessage(chatId.toString(), text))

fun DefaultAbsSender.sendMarkdownText(chatId: Long, text: String): Message =
    execute(SendMessage(chatId.toString(), text).apply { enableMarkdown(true) })

fun DefaultAbsSender.sendText(chatId: Long, text: String, keyboard: List<List<InlineKeyboardButton>>) {
    execute(SendMessage(chatId.toString(), text).apply { replyMarkup = InlineKeyboardMarkup(keyboard) })
}

fun AbsSender.sendFile(chatId: Long, name: String, content: ByteArray): Message =
    execute(
        SendDocument().also {
            it.chatId = chatId.toString()
            it.document = InputFile(content.inputStream(), name)
        }
    )

fun DefaultAbsSender.sendCatPhoto(chatId: Long, title: String) {
    val path = if (title.isEmpty()) "/cat" else "/cat/says/${title.replace('/', ' ')}"
    val query = "filter=sepia&width=500&size=50&type=square&uuid=${UUID.randomUUID()}"
    val uri = URI("https", "cataas.com", path, query, null)
    execute(SendPhoto(chatId.toString(), InputFile(uri.toASCIIString())))
}

fun DefaultAbsSender.downloadFileById(fileId: String): File {
    val fileInfo = execute(GetFile().also { it.fileId = fileId })
    return downloadFile(fileInfo)
}
