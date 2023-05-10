package org.sirekanyan.rknfreebot.extensions

import com.github.f4b6a3.uuid.codec.base.Base62Codec
import java.util.*

private val base62 = Base62Codec.INSTANCE

fun randomUuidBase62(): String =
    base62.encode(UUID.randomUUID())
