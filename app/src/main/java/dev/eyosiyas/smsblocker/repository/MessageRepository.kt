package dev.eyosiyas.smsblocker.repository

import androidx.lifecycle.LiveData
import dev.eyosiyas.smsblocker.dao.MessageDao
import dev.eyosiyas.smsblocker.model.Message

class MessageRepository(private val messageDao: MessageDao) {
    val messages: LiveData<List<Message>> = messageDao.messages()
    suspend fun addMessage(message: Message) {
        messageDao.addMessage(message)
    }

    suspend fun deleteMessage(message: Message) {
        messageDao.deleteMessage(message)
    }

    suspend fun deleteMessages() {
        messageDao.deleteMessages()
    }
}