package com.sirekanyan.rknfreebot.command

import com.sirekanyan.rknfreebot.Controller

interface Command {

    fun execute(controller: Controller, message: String?): Boolean

}