package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Blacklist

interface BlacklistSelected {
    fun onUpdateSelected(blacklist: Blacklist)
    fun onDeleteSelected(blacklist: Blacklist)
}