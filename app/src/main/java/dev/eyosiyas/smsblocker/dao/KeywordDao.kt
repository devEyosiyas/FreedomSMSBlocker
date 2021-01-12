package dev.eyosiyas.smsblocker.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.eyosiyas.smsblocker.model.Keyword

@Dao
interface KeywordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addKeyword(keyword: Keyword)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateKeyword(keyword: Keyword)

    @Delete
    suspend fun deleteKeyword(keyword: Keyword)

    @Query("DELETE FROM Keyword")
    suspend fun deleteKeywords()

    @Query("SELECT * FROM Keyword")
    fun keywords(): LiveData<List<Keyword>>

    @Query("SELECT * FROM Keyword WHERE keyword = :input")
    fun exists(input: String): Boolean

}