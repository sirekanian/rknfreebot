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
    fun start(code: String?)
    fun invite(id: String?)
    fun help(id: String?)
    fun getKey(location: String?)
    fun showCat(id: String?)
    fun showStatus(id: String?)
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
    private val resources = createResources(user.languageCode)

    override fun start(code: String?) {
        when {
            isAuthorized() -> {
                showLocationButtons(resources.helloMessage)
                sender.logInfo("#chat$chatId ($username) is #started")
            }
            code == null -> {
                sender.sendText(chatId, resources.notAuthorized)
                sender.logInfo("#chat$chatId ($username) is #unauthorized")
            }
            userRepository.hasInvite(code) -> {
                userRepository.addChat(chatId)
                showLocationButtons(resources.welcomeMessage)
                sender.logInfo("#chat$chatId <= #invite$code")
            }
            else -> {
                sender.sendText(chatId, resources.invitationExpired)
            }
        }
    }

    override fun invite(id: String?) {
        if (id != null) {
            sender.sendText(chatId, resources.invitationExpired)
            return
        }
        if (!isAuthorized()) {
            sender.sendText(chatId, resources.notAuthorized)
            return
        }
        val code = randomUuidBase62()
        sender.sendCoupon(chatId, resources.shareButton, resources.shareText, code)
        userRepository.addInvite(code)
        sender.logInfo("#chat$chatId => #invite$code")
    }

    private fun isAuthorized(): Boolean =
        isAdmin || userRepository.hasChat(chatId)

    override fun help(id: String?) {
        sender.sendMarkdownText(chatId, resources.help)
    }

    override fun getKey(location: String?) {
        if (location == null) return
        val key = repository.ejectKey(location)
        if (key == null) {
            showLocationButtons(resources.emptyKeysForLocation)
            return
        }
        sender.logInfo("Sending $location$OVPN_EXTENSION to #chat$chatId ($username)")
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

    override fun showStatus(id: String?) {
        if (isAdmin) {
            val counts = repository.getCounts().toSortedMap()
            val total = join("Total" to counts.values.sum())
            sender.logInfo(counts.toList().joinToString("\n", postfix = "\n") { join(it) } + total)
        }
    }

    private fun join(pair: Pair<String, Long>): String =
        "${pair.first}: ${pair.second}"

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
        val locations = repository.getLocations().sorted()
        if (locations.isEmpty()) {
            sender.sendText(chatId, resources.emptyLocations)
            return
        }
        val buttons = locations.map { location ->
            InlineKeyboardButton(location).apply { callbackData = "/get $location" }
        }
        sender.sendText(chatId, text, buttons.chunked(3))
    }

}