package com.sirekanyan.rknfreebot.command

import com.sirekanyan.rknfreebot.Controller
import com.sirekanyan.rknfreebot.botName
import java.util.regex.Pattern
import kotlin.text.RegexOption.IGNORE_CASE

class RegularCommand(
    private val words: List<String>,
    private val action: ((Controller, String?) -> Unit)? = null,
) : Command {

    override fun execute(controller: Controller, message: String?): Boolean {
        val argument = parseArgument(message)
        when {
            argument == null -> return false
            argument.isBlank() -> action?.invoke(controller, null)
            else -> action?.invoke(controller, argument)
        }
        return true
    }

    private fun parseArgument(text: String?): String? {
        val commands = words.flatMap { if (it.startsWith('/')) listOf(it, "$it@$botName") else listOf(it) }
        val regex = Regex("(${commands.joinToString("|", transform = Pattern::quote)})( (.*))?", IGNORE_CASE)
        return regex.matchEntire(text.orEmpty())?.groupValues?.last()
    }

}