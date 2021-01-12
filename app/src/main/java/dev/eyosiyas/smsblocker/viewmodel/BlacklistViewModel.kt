package dev.eyosiyas.smsblocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.repository.BlacklistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlacklistViewModel(app: Application) : AndroidViewModel(app) {
    internal val blacklists: LiveData<List<Blacklist>>
    private val repository: BlacklistRepository

    init {
        val blacklistDao = Storage.database(app).blacklistDao()
        repository = BlacklistRepository(blacklistDao)
        blacklists = repository.blacklists
    }

    fun addBlacklist(blacklist: Blacklist) {
        viewModelScope.launch(Dispatchers.IO) { repository.addBlacklist(blacklist) }
    }

    fun updateBlacklist(blacklist: Blacklist) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateBlacklist(blacklist) }
    }

    fun deleteBlacklist(blacklist: Blacklist) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBlacklist(blacklist) }
    }

    fun deleteBlacklists() {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBlacklists() }
    }

    suspend fun exists(blacklist: String): Boolean {
        var response: Boolean
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) { response = repository.exists(blacklist) }
        return response
    }
}