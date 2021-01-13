package dev.eyosiyas.smsblocker.repository

import androidx.lifecycle.LiveData
import dev.eyosiyas.smsblocker.dao.KeywordDao
import dev.eyosiyas.smsblocker.model.Keyword

class KeywordRepository(private val keywordDao: KeywordDao) {
    val keywords: LiveData<List<Keyword>> = keywordDao.keywords()
    suspend fun addKeyword(keyword: Keyword) {
        keywordDao.addKeyword(keyword)
    }

    suspend fun updateKeyword(keyword: Keyword) {
        keywordDao.updateKeyword(keyword)
    }

    suspend fun deleteKeyword(keyword: Keyword) {
        keywordDao.deleteKeyword(keyword)
    }

    suspend fun deleteKeywords() {
        keywordDao.deleteKeywords()
    }

    fun exists(input: String): Boolean {
        return keywordDao.exists(input)
    }

    fun keywordsCount(): Int {
        return keywordDao.keywordsCount()
    }


}