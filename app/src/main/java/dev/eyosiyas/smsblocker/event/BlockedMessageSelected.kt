package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Blocked

interface BlockedMessageSelected {
    fun onDeleteSelected(blockedMessage: Blocked)
}