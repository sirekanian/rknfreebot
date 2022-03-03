package com.sirekanyan.rknfreebot

import com.sirekanyan.rknfreebot.extensions.logError
import com.sirekanyan.rknfreebot.extensions.sendCatPhoto
import com.sirekanyan.rknfreebot.extensions.sendText
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Message

interface Controller {
    val data: String
    fun showCat(id: String?)
}

class ControllerImpl(
    override val data: String,
    private val sender: DefaultAbsSender,
    message: Message,
) : Controller {

    private val chatId = message.chatId
    private val isAdmin = adminId == chatId.toString()

    override fun showCat(id: String?) {
        try {
            sender.sendCatPhoto(chatId, "Hello")
        } catch (exception: Exception) {
            sender.sendText(chatId, "Hi")
            sender.logError("Cannot send a cat", exception)
        }
    }

}