package com.sirekanyan.rknfreebot

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class ControllerFactory {

    fun createController(sender: Bot, update: Update): Controller {
        val message: Message
        val user: User
        val data: String
        when {
            update.hasMessage() -> {
                message = checkNotNull(update.message) { "message is empty" }
                user = message.from
                data = message.text
            }
            update.hasEditedMessage() -> {
                message = checkNotNull(update.editedMessage) { "edited message is empty" }
                user = message.from
                data = message.text
            }
            update.hasCallbackQuery() -> {
                val callback = checkNotNull(update.callbackQuery) { "callback is empty" }
                message = checkNotNull(callback.message) { "callback message is empty" }
                user = callback.from
                data = callback.data
            }
            else -> error("unknown type of update")
        }
        println("${user.id} (chat ${message.chatId}) => $data")
        return ControllerImpl(data, sender, message)
    }

}