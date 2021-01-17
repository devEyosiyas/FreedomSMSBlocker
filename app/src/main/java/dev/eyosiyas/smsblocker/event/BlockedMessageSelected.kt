package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Blocked

interface BlockedMessageSelected {
    fun onSelected(blockedMessage: Blocked)
    fun onDeleteSelected(blockedMessage: Blocked)
}