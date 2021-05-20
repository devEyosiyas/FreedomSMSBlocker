package dev.eyosiyas.smsblocker.util

import androidx.recyclerview.widget.DiffUtil
import dev.eyosiyas.smsblocker.model.Message

class MessageDiffUtil(private val oldMessages: List<Message>, private val newMessages: List<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldMessages.size
    }

    override fun getNewListSize(): Int {
        return newMessages.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessages[oldItemPosition].timestamp == newMessages[newItemPosition].timestamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldMessages[oldItemPosition].sender != newMessages[newItemPosition].sender -> false
            oldMessages[oldItemPosition].body != newMessages[newItemPosition].body -> false
            oldMessages[oldItemPosition].type != newMessages[newItemPosition].type -> false
            oldMessages[oldItemPosition].timestamp != newMessages[newItemPosition].timestamp -> false
            oldMessages[oldItemPosition].isRead != newMessages[newItemPosition].isRead -> false
            oldMessages[oldItemPosition].isSeen != newMessages[newItemPosition].isSeen -> false
            else -> true
        }
    }
}