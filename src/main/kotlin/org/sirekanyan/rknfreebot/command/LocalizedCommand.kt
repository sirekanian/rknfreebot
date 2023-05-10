package org.sirekanyan.rknfreebot.command

import org.sirekanyan.rknfreebot.Controller
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand

class LocalizedCommand(
    word: String,
    action: (Controller, String?) -> Unit,
    enDescription: String,
    ruDescription: String,
) : Command by RegularCommand(listOf(word), action) {

    val en = BotCommand(word, enDescription)

    val ru = BotCommand(word, ruDescription)

}