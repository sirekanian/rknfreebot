package org.sirekanyan.rknfreebot.command

import org.sirekanyan.rknfreebot.Controller
import org.telegram.telegrambots.meta.api.objects.Message

fun interface Command {

    fun execute(controller: Controller, message: Message): Boolean

}