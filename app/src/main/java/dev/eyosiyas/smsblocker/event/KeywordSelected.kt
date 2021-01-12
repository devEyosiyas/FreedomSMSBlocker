package dev.eyosiyas.smsblocker.event

import dev.eyosiyas.smsblocker.model.Keyword

interface KeywordSelected {
    fun onInsertSelected(keyword: Keyword)
    fun onUpdateSelected(keyword: Keyword)
    fun onDeleteSelected(keyword: Keyword)
}