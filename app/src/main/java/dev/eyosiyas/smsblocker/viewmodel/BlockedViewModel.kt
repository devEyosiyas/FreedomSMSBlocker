package dev.eyosiyas.smsblocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Blocked
import dev.eyosiyas.smsblocker.repository.BlockedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlockedViewModel(app: Application) : AndroidViewModel(app) {
    internal val blockedMessages: LiveData<List<Blocked>>
    private val repository: BlockedRepository

    init {
        val blockedDao = Storage.database(app).blockedDao()
        repository = BlockedRepository(blockedDao)
        blockedMessages = repository.blockedMessages
    }

    fun addBlocked(blocked: Blocked) {
        viewModelScope.launch(Dispatchers.IO) { repository.addBlocked(blocked) }
    }

    fun deleteBlocked(blocked: Blocked) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBlocked(blocked) }
    }

    fun deleteBlockedLists() {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBlockedList() }
    }
}