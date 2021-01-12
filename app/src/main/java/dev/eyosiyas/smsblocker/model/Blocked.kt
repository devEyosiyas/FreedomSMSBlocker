package dev.eyosiyas.smsblocker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Blocked")
data class Blocked(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val sender: String,
        val message: String,
        val timestamp: Long
)
