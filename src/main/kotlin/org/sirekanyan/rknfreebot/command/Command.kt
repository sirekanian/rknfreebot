package org.sirekanyan.rknfreebot.command

import org.sirekanyan.rknfreebot.Controller

interface Command {

    fun execute(controller: Controller, message: String?): Boolean

}