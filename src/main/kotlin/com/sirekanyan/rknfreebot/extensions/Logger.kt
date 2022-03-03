package com.sirekanyan.rknfreebot.extensions

import com.sirekanyan.rknfreebot.adminId
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun AbsSender.logInfo(text: String) {
    println(text)
    execute(SendMessage(adminId, text))
}

fun AbsSender.logError(text: String, throwable: Throwable) {
    try {
        throwable.printStackTrace()
        val stream = ByteArrayOutputStream()
        throwable.printStackTrace(PrintStream(stream))
        val input = stream.toString().byteInputStream()
        val document = InputFile(input, "stacktrace.txt")
        execute(SendDocument(adminId, document).also { it.caption = text })
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

fun AbsSender.logError(update: Update) {
    try {
        val input = update.toString().byteInputStream()
        val document = InputFile(input, "update.txt")
        execute(SendDocument(adminId, document))
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}
