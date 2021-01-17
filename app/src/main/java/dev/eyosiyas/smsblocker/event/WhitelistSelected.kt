package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Whitelist

interface WhitelistSelected {
    fun onUpdateSelected(whitelist: Whitelist)
    fun onDeleteSelected(whitelist: Whitelist)
}