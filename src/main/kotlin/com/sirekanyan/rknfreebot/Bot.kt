package com.sirekanyan.rknfreebot

import com.sirekanyan.rknfreebot.command.Command
import com.sirekanyan.rknfreebot.command.RegularCommand
import com.sirekanyan.rknfreebot.config.Config
import com.sirekanyan.rknfreebot.config.ConfigKey.*
import com.sirekanyan.rknfreebot.extensions.logError
import com.sirekanyan.rknfreebot.extensions.logInfo
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.util.WebhookUtils

val adminId = Config[ADMIN_ID]
val botName = Config[BOT_USERNAME]
private val commands: List<Command> =
    listOf(
        RegularCommand(listOf("/start"), Controller::start),
        RegularCommand(listOf("/invite"), Controller::invite),
        RegularCommand(listOf("/get"), Controller::getKey),
        RegularCommand(listOf("/cat"), Controller::showCat),
        RegularCommand(listOf("/stat"), Controller::showStat),
    )

class Bot : DefaultAbsSender(DefaultBotOptions()), LongPollingBot {

    private val factory = ControllerFactory()

    override fun getBotUsername(): String = botName

    override fun getBotToken(): String = Config[BOT_TOKEN]

    override fun onUpdateReceived(update: Update) {
        try {
            onUpdate(update)
        } catch (exception: Exception) {
            logError("Cannot handle update", exception)
            logError(update)
        }
    }

    private fun onUpdate(update: Update) {
        val controller = factory.createController(this, update)
        update.message?.document?.let { document ->
            controller.onDocument(document)
            return
        }
        for (command in commands) {
            if (command.execute(controller, controller.data)) {
                return
            }
        }
    }

    override fun clearWebhook() {
        logInfo("Cleared.")
        WebhookUtils.clearWebhook(this)
    }

}