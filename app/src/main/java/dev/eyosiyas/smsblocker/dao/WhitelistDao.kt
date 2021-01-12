package dev.eyosiyas.smsblocker.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.eyosiyas.smsblocker.model.Whitelist

@Dao
interface WhitelistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWhitelist(whitelist: Whitelist)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateWhitelist(whitelist: Whitelist)

    @Delete
    suspend fun deleteWhitelist(whitelist: Whitelist)

    @Query("DELETE FROM Whitelist")
    suspend fun deleteWhitelists()

    @Query("SELECT * FROM Whitelist WHERE number = :number")
    fun exists(number: String): Boolean

    @Query("SELECT * FROM whitelist")
    fun whitelists(): LiveData<List<Whitelist>>
}