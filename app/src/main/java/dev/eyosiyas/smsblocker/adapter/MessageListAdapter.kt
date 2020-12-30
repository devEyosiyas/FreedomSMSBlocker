package dev.eyosiyas.smsblocker.adapter

import android.content.Context
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Core

class MessageListAdapter constructor(private val messages: List<Message?>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            return SentVH(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            return ReceivedVH(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message? = messages.get(position)
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
        if (message!!.type.equals("received", ignoreCase = true)) return VIEW_TYPE_MESSAGE_RECEIVED else return VIEW_TYPE_MESSAGE_SENT
    }

    inner class SentVH constructor(itemView: View) : RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        private val message: TextView = itemView.findViewById(R.id.txtSentMessage)
        private val time: TextView = itemView.findViewById(R.id.txtMessageSentTime)
        fun bind(sentMessage: Message?) {
            message.text = sentMessage!!.body
            time.text = Core.readableTime(sentMessage.timestamp)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            getContextMenu(menu, v)
        }

    }

    inner class ReceivedVH constructor(itemView: View) : RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        private val profile: ImageView = itemView.findViewById(R.id.SenderProfile)
        private val message: TextView = itemView.findViewById(R.id.txtSenderMessage)
        private val time: TextView = itemView.findViewById(R.id.txtSenderTimestamp)

        // TODO: 9/10/2020 Acquire profile picture
        fun bind(receivedMessage: Message?) {
            message.text = receivedMessage!!.body
            time.text = Core.readableTime(receivedMessage.timestamp)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            getContextMenu(menu, v)
        }

    }

    private fun getContextMenu(menu: ContextMenu, v: View) {
        menu.setHeaderTitle("Message options")
        menu.add(0, v.id, 0, "Delete").setOnMenuItemClickListener {
            Toast.makeText(context, "Delete coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
        menu.add(0, v.id, 0, "Copy text").setOnMenuItemClickListener {
            Toast.makeText(context, "Copy text coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
        menu.add(0, v.id, 0, "Forward").setOnMenuItemClickListener {
            Toast.makeText(context, "Forward text coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT: Int = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED: Int = 2
    }
}