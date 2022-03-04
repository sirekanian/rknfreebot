package com.sirekanyan.rknfreebot

import com.sirekanyan.rknfreebot.config.Config
import com.sirekanyan.rknfreebot.config.ConfigKey.DB_URL
import com.sirekanyan.rknfreebot.repository.KeyRepositoryImpl
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class ControllerFactory {

    private val repository = KeyRepositoryImpl(Config[DB_URL])

    fun createController(sender: Bot, update: Update): Controller {
        val message: Message
        val user: User
        val data: String
        when {
            update.hasMessage() || update.hasEditedMessage() -> {
                message = checkNotNull(update.message ?: update.editedMessage) { "message is empty" }
                user = message.from
                data = when {
                    message.hasDocument() -> message.caption.orEmpty()
                    else -> message.text
                }
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
        return ControllerImpl(data, sender, repository, message)
    }

}