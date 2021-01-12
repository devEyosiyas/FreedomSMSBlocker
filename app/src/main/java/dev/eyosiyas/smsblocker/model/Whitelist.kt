package dev.eyosiyas.smsblocker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Whitelist")
data class Whitelist(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val number: String,
        val timestamp: Long
)