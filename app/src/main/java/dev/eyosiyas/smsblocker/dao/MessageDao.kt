package dev.eyosiyas.smsblocker.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.eyosiyas.smsblocker.model.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("DELETE FROM Message")
    suspend fun deleteMessages()

    @Query("SELECT * FROM Message")
    fun messages(): LiveData<List<Message>>
}