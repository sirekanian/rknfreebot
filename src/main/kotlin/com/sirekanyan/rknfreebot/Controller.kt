package com.sirekanyan.rknfreebot

import com.sirekanyan.rknfreebot.extensions.downloadFileById
import com.sirekanyan.rknfreebot.extensions.logError
import com.sirekanyan.rknfreebot.extensions.sendCatPhoto
import com.sirekanyan.rknfreebot.extensions.sendText
import com.sirekanyan.rknfreebot.repository.KeyRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

interface Controller {
    val data: String
    fun start(id: String?)
    fun showCat(id: String?)
    fun onDocument(document: Document)
}

class ControllerImpl(
    override val data: String,
    private val sender: DefaultAbsSender,
    private val repository: KeyRepository,
    message: Message,
) : Controller {

    private val chatId = message.chatId
    private val isAdmin = adminId == chatId.toString()

    override fun start(id: String?) {
        val text = "Select server location. Note that location near to you may be the best choice."
        val locations = repository.getLocations()
        val buttons = locations.map { location ->
            InlineKeyboardButton(location).apply { callbackData = "/get $location" }
        }
        sender.sendText(chatId, text, buttons.chunked(4))
    }

    override fun showCat(id: String?) {
        try {
            sender.sendCatPhoto(chatId, "Hello")
        } catch (exception: Exception) {
            sender.sendText(chatId, "Hi")
            sender.logError("Cannot send a cat", exception)
        }
    }

    override fun onDocument(document: Document) {
        val ovpnExtension = ".ovpn"
        val locationDelimiter = "-"
        if (isAdmin && document.fileName.endsWith(ovpnExtension)) {
            val documentName = document.fileName.removeSuffix(ovpnExtension)
            val location = documentName.substringBeforeLast(locationDelimiter)
            val index = documentName.substringAfterLast(locationDelimiter).toInt()
            val file = sender.downloadFileById(document.fileId)
            try {
                repository.saveKey(location, index, file)
                sender.sendText(chatId, "${document.fileName} saved")
            } catch (exception: ExposedSQLException) {
                sender.logError("Cannot save ${document.fileName}", exception)
            }
        }
    }

}