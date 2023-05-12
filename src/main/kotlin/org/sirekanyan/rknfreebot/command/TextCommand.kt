package org.sirekanyan.rknfreebot.command

import org.sirekanyan.rknfreebot.Controller

interface TextCommand {

    fun execute(controller: Controller, text: String?): Boolean

}