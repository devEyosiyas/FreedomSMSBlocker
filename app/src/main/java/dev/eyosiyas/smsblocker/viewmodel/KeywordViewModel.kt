package dev.eyosiyas.smsblocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Keyword
import dev.eyosiyas.smsblocker.repository.KeywordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KeywordViewModel(app: Application) : AndroidViewModel(app) {
    internal val keywords: LiveData<List<Keyword>>
    private val repository: KeywordRepository

    init {
        val keywordDao = Storage.database(app).keywordDao()
        repository = KeywordRepository(keywordDao)
        keywords = repository.keywords
    }

    fun addKeyword(keyword: Keyword) {
        viewModelScope.launch(Dispatchers.IO) { repository.addKeyword(keyword) }
    }

    fun updateKeyword(keyword: Keyword) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateKeyword(keyword) }
    }

    fun deleteKeyword(keyword: Keyword) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteKeyword(keyword) }
    }

    fun deleteKeywords() {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteKeywords() }
    }

    suspend fun exists(keyword: String): Boolean {
        var response: Boolean
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) { response = repository.exists(keyword) }
        return response
    }

    suspend fun keywordsCount(): Int {
        var count: Int
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) { count = repository.keywordsCount() }
        return count
    }
}