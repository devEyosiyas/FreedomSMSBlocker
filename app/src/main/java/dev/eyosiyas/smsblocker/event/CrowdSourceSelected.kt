package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Blacklist

interface CrowdSourceSelected {
    fun onInsertSelected(blacklist: Blacklist)
    fun onLongPress(position: Int)
    fun onActivate(show: Boolean)
}