package dev.eyosiyas.smsblocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Whitelist
import dev.eyosiyas.smsblocker.repository.WhitelistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WhitelistViewModel(app: Application) : AndroidViewModel(app) {
    internal val whitelists: LiveData<List<Whitelist>>
    private val repository: WhitelistRepository

    init {
        val whitelistDao = Storage.database(app).whitelistDao()
        repository = WhitelistRepository(whitelistDao)
        whitelists = repository.whitelists
    }

    fun addWhitelist(whitelist: Whitelist) {
        viewModelScope.launch(Dispatchers.IO) { repository.addWhitelist(whitelist) }
    }

    fun updateWhitelist(whitelist: Whitelist) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateWhitelist(whitelist) }
    }

    fun deleteWhitelist(whitelist: Whitelist) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteWhitelist(whitelist) }
    }

    fun deleteWhitelists() {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteWhitelists() }
    }

    suspend fun exists(keyword: String): Boolean {
        var response: Boolean
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) { response = repository.exists(keyword) }
        return response
    }

    suspend fun whitelistCount(): Int {
        var count: Int
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) { count = repository.whitelistCount() }
        return count
    }
}