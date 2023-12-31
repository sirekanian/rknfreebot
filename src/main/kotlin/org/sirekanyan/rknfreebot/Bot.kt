package org.sirekanyan.rknfreebot

import org.sirekanyan.rknfreebot.command.Command
import org.sirekanyan.rknfreebot.command.LocalizedCommand
import org.sirekanyan.rknfreebot.command.RegularCommand
import org.sirekanyan.rknfreebot.command.TextCommand
import org.sirekanyan.rknfreebot.config.Config
import org.sirekanyan.rknfreebot.config.ConfigKey.*
import org.sirekanyan.rknfreebot.extensions.logError
import org.sirekanyan.rknfreebot.extensions.logInfo
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.util.WebhookUtils

val adminId = Config[ADMIN_ID]
val botName = Config[BOT_USERNAME]
val botToken = Config[BOT_TOKEN]
private val commands: List<Command> =
    listOf(
        Command(Controller::onDocument),
    )
private val textCommands: List<TextCommand> =
    listOf(
        LocalizedCommand("/start", Controller::start, "get a key for free", "получить ключ бесплатно"),
        LocalizedCommand("/invite", Controller::invite, "invite a friend", "пригласить друга"),
        LocalizedCommand("/help", Controller::help, "help", "как пользоваться"),
        RegularCommand(listOf("/get"), Controller::getKey),
        RegularCommand(listOf("/cat"), Controller::showCat),
        RegularCommand(listOf("/status"), Controller::showStatus),
    )

class Bot : DefaultAbsSender(DefaultBotOptions(), botToken), LongPollingBot {

    private val factory = ControllerFactory()

    init {
        val localizedCommands = textCommands.filterIsInstance<LocalizedCommand>()
        val ruCommands = localizedCommands.map(LocalizedCommand::ru)
        val enCommands = localizedCommands.map(LocalizedCommand::en)
        val scope = BotCommandScopeDefault.builder().build()
        execute(SetMyCommands(ruCommands, scope, "ru"))
        execute(SetMyCommands(enCommands, scope, null))
    }

    override fun getBotUsername(): String = botName

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
        (update.message ?: update.editedMessage)?.let { message ->
            for (command in commands) {
                if (command.execute(controller, message)) {
                    return
                }
            }
        }
        for (command in textCommands) {
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