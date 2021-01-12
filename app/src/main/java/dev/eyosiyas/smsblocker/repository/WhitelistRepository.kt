package dev.eyosiyas.smsblocker.repository

import androidx.lifecycle.LiveData
import dev.eyosiyas.smsblocker.dao.WhitelistDao
import dev.eyosiyas.smsblocker.model.Whitelist

class WhitelistRepository(private val whitelistDao: WhitelistDao) {
    val whitelists: LiveData<List<Whitelist>> = whitelistDao.whitelists()

    suspend fun addWhitelist(whitelist: Whitelist) {
        whitelistDao.addWhitelist(whitelist)
    }

    suspend fun updateWhitelist(whitelist: Whitelist) {
        whitelistDao.updateWhitelist(whitelist)
    }

    suspend fun deleteWhitelist(whitelist: Whitelist) {
        whitelistDao.deleteWhitelist(whitelist)
    }

    suspend fun deleteWhitelists() {
        whitelistDao.deleteWhitelists()
    }

    fun exists(input: String): Boolean {
        return whitelistDao.exists(input)
    }
}