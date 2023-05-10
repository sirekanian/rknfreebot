package org.sirekanyan.rknfreebot

import org.sirekanyan.rknfreebot.config.Config
import org.sirekanyan.rknfreebot.config.ConfigKey.DB_URL
import org.sirekanyan.rknfreebot.repository.KeyRepositoryImpl
import org.sirekanyan.rknfreebot.repository.UserRepositoryImpl
import org.jetbrains.exposed.sql.Database
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class ControllerFactory {

    init {
        Database.connect(Config[DB_URL])
    }

    private val repository = KeyRepositoryImpl()
    private val userRepository = UserRepositoryImpl()

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
        return ControllerImpl(data, sender, repository, userRepository, message, user)
    }

}