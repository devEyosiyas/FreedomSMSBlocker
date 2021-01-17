package dev.eyosiyas.smsblocker.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.eyosiyas.smsblocker.model.Blocked

@Dao
interface BlockedDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBlocked(blocked: Blocked)

    @Delete
    suspend fun deleteBlocked(blocked: Blocked)

    @Query("DELETE FROM Blocked")
    suspend fun deleteBlockedList()

    @Query("SELECT * FROM Blocked")
    fun blockedMessages(): LiveData<List<Blocked>>
}