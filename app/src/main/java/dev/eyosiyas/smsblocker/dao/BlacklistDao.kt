package dev.eyosiyas.smsblocker.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.eyosiyas.smsblocker.model.Blacklist

@Dao
interface BlacklistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBlacklist(blacklist: Blacklist)

    @Query("SELECT * FROM Blacklist")
    fun blacklists(): LiveData<List<Blacklist>>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateBlacklist(blacklist: Blacklist)

    @Delete
    suspend fun deleteBlacklist(blacklist: Blacklist)

    @Query("DELETE FROM Blacklist")
    suspend fun deleteBlacklists()

    @Query("SELECT * FROM Blacklist WHERE number = :number")
    fun exists(number: String): Boolean
}