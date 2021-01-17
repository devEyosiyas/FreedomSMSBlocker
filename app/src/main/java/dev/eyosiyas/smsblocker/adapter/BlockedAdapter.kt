package dev.eyosiyas.smsblocker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.BlockedMessageBinding
import dev.eyosiyas.smsblocker.event.BlockedMessageSelected
import dev.eyosiyas.smsblocker.model.Blocked
import dev.eyosiyas.smsblocker.util.Core

class BlockedAdapter(private val blockedMessageSelected: BlockedMessageSelected) : RecyclerView.Adapter<BlockedAdapter.ViewHolder>() {
    private var blockedMessages = emptyList<Blocked>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.blocked_message, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blockedMessage = blockedMessages[position]
        holder.binding.blockedMessageNumber.text = blockedMessage.sender
        holder.binding.blockedMessageTimestamp.text = Core.readableTime(blockedMessage.timestamp, holder.itemView.context)
        holder.binding.deleteBlockedMessage.setOnClickListener { blockedMessageSelected.onDeleteSelected(blockedMessage) }
        holder.binding.container.setOnClickListener { blockedMessageSelected.onSelected(blockedMessage) }
    }


    override fun getItemCount(): Int {
        return blockedMessages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: BlockedMessageBinding = BlockedMessageBinding.bind(itemView)
    }

    fun populate(blockedMessages: List<Blocked>) {
        this.blockedMessages = blockedMessages
        notifyDataSetChanged()
    }
}