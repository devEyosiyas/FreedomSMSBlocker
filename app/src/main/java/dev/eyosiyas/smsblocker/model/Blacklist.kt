package dev.eyosiyas.smsblocker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Blacklist")
data class Blacklist(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val number: String,
        val timestamp: Long,
        val source: String,
        val shared: Boolean
)