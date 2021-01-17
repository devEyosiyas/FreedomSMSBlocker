package dev.eyosiyas.smsblocker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.SmsItemBinding
import dev.eyosiyas.smsblocker.event.MessageSelected
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Core

class MessageAdapter(private val selectedMessage: MessageSelected, private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.sender != message.displayName) holder.binder.txtSenderName.text = message.displayName else holder.binder.txtSenderName.text = message.sender
        holder.binder.txtMessageBody.text = message.body
        holder.binder.txtTime.text = Core.readableTime(message.timestamp, holder.itemView.context)
        holder.itemView.setOnClickListener { selectedMessage.onMessageSelected(message, holder.binder.txtSenderName.text.toString()) }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binder: SmsItemBinding = SmsItemBinding.bind(itemView)
    }
}