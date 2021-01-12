package dev.eyosiyas.smsblocker.repository

import androidx.lifecycle.LiveData
import dev.eyosiyas.smsblocker.dao.BlockedDao
import dev.eyosiyas.smsblocker.model.Blocked

class BlockedRepository(private val blockedDao: BlockedDao) {
    val blockedMessages: LiveData<List<Blocked>> = blockedDao.blockedMessages()
    suspend fun addBlocked(blocked: Blocked) {
        blockedDao.addBlocked(blocked)
    }

    suspend fun deleteBlocked(blocked: Blocked) {
        blockedDao.deleteBlocked(blocked)
    }

    suspend fun deleteBlockedList() {
        blockedDao.deleteBlockedList()
    }
}