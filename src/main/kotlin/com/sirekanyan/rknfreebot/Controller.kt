package com.sirekanyan.rknfreebot

import com.sirekanyan.rknfreebot.extensions.*
import com.sirekanyan.rknfreebot.repository.KeyRepository
import com.sirekanyan.rknfreebot.repository.UserRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Document
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

interface Controller {
    val data: String
    fun start(id: String?)
    fun invite(code: String?)
    fun getKey(location: String?)
    fun showCat(id: String?)
    fun onDocument(document: Document)
}

class ControllerImpl(
    override val data: String,
    private val sender: DefaultAbsSender,
    private val repository: KeyRepository,
    private val userRepository: UserRepository,
    message: Message,
    user: User,
) : Controller {

    private val chatId = message.chatId
    private val username = user.getFullName()
    private val isAdmin = adminId == chatId.toString()

    override fun start(id: String?) {
        if (!isAuthorized()) {
            sender.sendText(chatId, "Ask to other users to send you an invitation code.")
            sender.logInfo("#chat$chatId ($username) is #unauthorized")
            return
        }
        showLocationButtons("Select server location. Note that location near to you may be the best choice.")
        sender.logInfo("#chat$chatId ($username) is #started")
    }

    override fun invite(code: String?) {
        if (code == null) {
            sendInvitation()
        } else {
            receiveInvitation(code)
        }
    }

    private fun sendInvitation() {
        if (!isAuthorized()) {
            sender.sendText(chatId, "You're not invited yet. Ask other users to send you an invite.")
            return
        }
        val code = randomUuidBase62()
        sender.sendText(chatId, "Ask your friend to send the next command to the bot @$botName.")
        sender.sendMarkdownText(chatId, "`/invite $code`")
        userRepository.addInvite(code)
        sender.logInfo("#chat$chatId => #invite$code")
    }

    private fun receiveInvitation(code: String) {
        if (!userRepository.hasInvite(code)) {
            sender.sendText(chatId, "Invitation code was expired, ask to your friend for another one.")
            return
        }
        userRepository.addChat(chatId)
        showLocationButtons("Hi and welcome! Select server location.")
        sender.logInfo("#chat$chatId <= #invite$code")
    }

    private fun isAuthorized(): Boolean =
        isAdmin || userRepository.hasChat(chatId)

    override fun getKey(location: String?) {
        if (location == null) {
            showLocationButtons("Select server location.")
            return
        }
        val index = repository.assignKey(location, chatId.toString())
        if (index == null) {
            showLocationButtons("There are no available keys in this location. Try another one.")
            return
        }
        sender.logInfo("Sending $location-$index$OVPN_EXTENSION to #chat$chatId ($username)")
        val key = repository.getKey(location, index)
        sender.sendFile(chatId, location + OVPN_EXTENSION, key)
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
        if (isAdmin && document.fileName.endsWith(OVPN_EXTENSION)) {
            val documentName = document.fileName.removeSuffix(OVPN_EXTENSION)
            val location = documentName.substringBeforeLast(LOCATION_DELIMITER)
            val index = documentName.substringAfterLast(LOCATION_DELIMITER).toInt()
            val file = sender.downloadFileById(document.fileId)
            try {
                repository.saveKey(location, index, file)
                sender.sendText(chatId, "${document.fileName} saved")
            } catch (exception: ExposedSQLException) {
                sender.logError("Cannot save ${document.fileName}", exception)
            }
        }
    }

    private fun showLocationButtons(text: String) {
        val locations = repository.getLocations()
        if (locations.isEmpty()) {
            sender.sendText(chatId, "There are no available keys at the moment. Please try later.")
            return
        }
        val buttons = locations.map { location ->
            InlineKeyboardButton(location).apply { callbackData = "/get $location" }
        }
        sender.sendText(chatId, text, buttons.chunked(4))
    }

}