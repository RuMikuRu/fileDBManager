package util

import androidx.compose.ui.text.toLowerCase
import model.Role

fun String.toBoolean(): Boolean {
    return when (this.toLowerCase()) {
        "false" -> false
        "true" -> true
        else -> {
            throw Exception("$this is not true/false")
        }
    }
}

fun String.toRole(): Role {
    return when (this) {
        "USER" -> Role.USER
        "ADMIN" -> Role.ADMIN
        else -> {
            throw Exception("$this is not ${Role.entries.toTypedArray()}")
        }
    }
}