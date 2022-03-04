package com.sirekanyan.rknfreebot.extensions

import org.telegram.telegrambots.meta.api.objects.User

fun User.getFullName(): String =
    listOfNotNull(
        firstName,
        lastName,
        userName?.let { "@$it" },
        "#id$id"
    ).joinToString(" ")
