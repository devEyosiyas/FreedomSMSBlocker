package dev.eyosiyas.smsblocker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import dev.eyosiyas.smsblocker.util.Constant.DEFAULT_PROFILE

@Entity
data class Message(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        @ColumnInfo(name = "sender_number")
        val sender: String,
        @ColumnInfo(name = "display_name")
        val displayName: String,
        val type: String,
        val body: String,
        @ColumnInfo(name = "is_read")
        val isRead: Boolean,
        @ColumnInfo(name = "is_seen")
        val isSeen: Boolean,
        val timestamp: Long
) {
    @Ignore
    var picture: String = DEFAULT_PROFILE
}