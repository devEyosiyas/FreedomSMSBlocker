package dev.eyosiyas.smsblocker.model

class Message(var type: String?, var isRead: Boolean, var isSeen: Boolean, var body: String?, var sender: String?, var displayName: String?, var image: String?, var timestamp: Long)