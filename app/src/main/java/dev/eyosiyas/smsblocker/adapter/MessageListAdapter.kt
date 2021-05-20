package dev.eyosiyas.smsblocker.adapter

import android.net.Uri
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.ItemMessageReceivedBinding
import dev.eyosiyas.smsblocker.databinding.ItemMessageSentBinding
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.MessageDiffUtil

class MessageListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //class MessageListAdapter constructor(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages = emptyList<Message>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_SENT)
            SentVH(LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false))
        else
            ReceivedVH(LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = messages[position]
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentVH).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedVH).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = messages[position]
        return if (message.type.equals("received", ignoreCase = true)) VIEW_TYPE_MESSAGE_RECEIVED else VIEW_TYPE_MESSAGE_SENT
    }

    fun populate(newMessages: List<Message>) {
        val results = DiffUtil.calculateDiff(MessageDiffUtil(messages, newMessages))
        messages = newMessages
        results.dispatchUpdatesTo(this)
    }

    inner class SentVH constructor(itemView: View) : RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        private val binder: ItemMessageSentBinding = ItemMessageSentBinding.bind(itemView)

        fun bind(sentMessage: Message?) {
            binder.txtSentMessage.text = sentMessage!!.body
            binder.txtMessageSentTime.text = Core.readableTime(sentMessage.timestamp, itemView.context)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            getContextMenu(menu, v)
        }

    }

    inner class ReceivedVH constructor(itemView: View) : RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        private val binder: ItemMessageReceivedBinding = ItemMessageReceivedBinding.bind(itemView)
        fun bind(receivedMessage: Message?) {
            binder.txtSenderMessage.text = receivedMessage!!.body
            binder.txtSenderTimestamp.text = Core.readableTime(receivedMessage.timestamp, itemView.context)
            itemView.setOnCreateContextMenuListener(this)
            val sender = if (receivedMessage.sender != receivedMessage.displayName) receivedMessage.displayName else receivedMessage.sender
            if (receivedMessage.picture == Constant.DEFAULT_PROFILE) {
                val initial: Char = sender.toCharArray()[0]
                if (receivedMessage.displayName != receivedMessage.sender) {
                    if (initial.isLetter())
                        binder.SenderProfile.setImageDrawable(TextDrawable.builder()
                                .buildRound(initial.toString(), ColorGenerator.MATERIAL.getColor(sender)))
                } else
                    binder.SenderProfile.setImageDrawable(ResourcesCompat.getDrawable(itemView.context.resources, R.drawable.ic_user, null))
            } else
                binder.SenderProfile.setImageURI(Uri.parse(receivedMessage.picture))

        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            getContextMenu(menu, v)
        }

    }

    private fun getContextMenu(menu: ContextMenu, v: View) {
        menu.setHeaderTitle(v.context.getString(R.string.message_options))
        menu.add(0, v.id, 0, R.string.delete).setOnMenuItemClickListener {
            Toast.makeText(v.context, "Delete coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
        menu.add(0, v.id, 0, R.string.copy_text).setOnMenuItemClickListener {
            Toast.makeText(v.context, "Copy text coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
        menu.add(0, v.id, 0, R.string.forward).setOnMenuItemClickListener {
            Toast.makeText(v.context, "Forward text coming soon!", Toast.LENGTH_SHORT).show()
            false
        }
    }


    companion object {


        private const val VIEW_TYPE_MESSAGE_SENT: Int = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED: Int = 2
    }
}