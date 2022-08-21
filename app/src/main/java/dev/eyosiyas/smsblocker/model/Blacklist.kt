package dev.eyosiyas.smsblocker.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Blacklist")
data class Blacklist(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val number: String,
        val timestamp: Long,
        val source: String,
        val shared: Boolean,

        ) {
    @Ignore
    var checked: Boolean = false
}