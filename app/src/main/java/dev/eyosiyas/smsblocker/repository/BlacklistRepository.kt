package dev.eyosiyas.smsblocker.repository

import androidx.lifecycle.LiveData
import dev.eyosiyas.smsblocker.dao.BlacklistDao
import dev.eyosiyas.smsblocker.model.Blacklist

class BlacklistRepository(private val blacklistDao: BlacklistDao) {
    val blacklists: LiveData<List<Blacklist>> = blacklistDao.blacklists()
    suspend fun addBlacklist(blacklist: Blacklist) {
        blacklistDao.addBlacklist(blacklist)
    }

    suspend fun updateBlacklist(blacklist: Blacklist) {
        blacklistDao.updateBlacklist(blacklist)
    }

    suspend fun deleteBlacklist(blacklist: Blacklist) {
        blacklistDao.deleteBlacklist(blacklist)
    }

    suspend fun deleteBlacklists() {
        blacklistDao.deleteBlacklists()
    }

    fun exists(input: String): Boolean {
        return blacklistDao.exists(input)
    }
}