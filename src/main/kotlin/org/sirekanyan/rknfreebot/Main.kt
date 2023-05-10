@file:JvmName("Main")

package org.sirekanyan.rknfreebot

import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    TelegramBotsApi(DefaultBotSession::class.java).registerBot(Bot())
}