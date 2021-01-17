package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Message

interface MessageSelected {
    fun onMessageSelected(message: Message, displayName: String)
}