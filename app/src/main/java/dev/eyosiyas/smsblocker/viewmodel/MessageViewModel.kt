package dev.eyosiyas.smsblocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.repository.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageViewModel(app: Application) : AndroidViewModel(app) {
    internal val messages: LiveData<List<Message>>
    private val repository: MessageRepository

    init {
        val messageDao = Storage.database(app).messageDao()
        repository = MessageRepository(messageDao)
        messages = repository.messages
    }

    fun addMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) { repository.addMessage(message) }
    }

    fun deleteMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteMessage(message) }
    }

    fun deleteMessages() {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteMessages() }
    }
}