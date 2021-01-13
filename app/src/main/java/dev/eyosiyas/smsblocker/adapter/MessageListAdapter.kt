package dev.eyosiyas.smsblocker.adapter

import android.content.Context
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.ItemMessageReceivedBinding
import dev.eyosiyas.smsblocker.databinding.ItemMessageSentBinding
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Core

class MessageListAdapter constructor(private val messages: List<Message?>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_SENT)
            SentVH(LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false))
        else
            ReceivedVH(LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message? = messages[position]
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentVH).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedVH).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message? = messages[position]
        return if (message!!.type.equals("received", ignoreCase = true)) VIEW_TYPE_MESSAGE_RECEIVED else VIEW_TYPE_MESSAGE_SENT
    }

    inner class SentVH constructor(itemView: View) : RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        private val binder: ItemMessageSentBinding = ItemMessageSentBinding.bind(itemView)

        fun bind(sentMessage: Message?) {
            binder.txtSentMessage.text = sentMessage!!.body
            binder.txtMessageSentTime.text = Core.readableTime(sentMessage.timestamp)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            getContextMenu(menu, v)
        }

    }

    inner class ReceivedVH constructor(itemView: View) : RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        private val binder: ItemMessageReceivedBinding = ItemMessageReceivedBinding.bind(itemView)

        // TODO: 9/10/2020 Acquire profile picture
        fun bind(receivedMessage: Message?) {
            binder.txtSenderMessage.text = receivedMessage!!.body
            binder.txtSenderTimestamp.text = Core.readableTime(receivedMessage.timestamp)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            getContextMenu(menu, v)
        }

    }

    private fun getContextMenu(menu: ContextMenu, v: View) {
        menu.setHeaderTitle(context.getString(R.string.message_options))
        menu.add(0, v.id, 0, R.string.delete).setOnMenuItemClickListener {
            Toast.makeText(context, "Delete coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
        menu.add(0, v.id, 0, R.string.copy_text).setOnMenuItemClickListener {
            Toast.makeText(context, "Copy text coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
        menu.add(0, v.id, 0, R.string.forward).setOnMenuItemClickListener {
            Toast.makeText(context, "Forward text coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT: Int = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED: Int = 2
    }
}