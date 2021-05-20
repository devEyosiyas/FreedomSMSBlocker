package dev.eyosiyas.smsblocker.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.SmsItemBinding
import dev.eyosiyas.smsblocker.event.MessageSelected
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Constant.DEFAULT_PROFILE
import dev.eyosiyas.smsblocker.util.Core


class MessageAdapter(private val selectedMessage: MessageSelected, private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setMessage(position)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binder: SmsItemBinding = SmsItemBinding.bind(itemView)

        fun setMessage(position: Int) {
            val message = messages[position]
            val sender = if (message.sender != message.displayName) message.displayName else message.sender
            binder.txtSenderName.text = sender
            if (message.picture == DEFAULT_PROFILE) {
                val initial: Char = sender.toCharArray()[0]
                if (message.displayName != message.sender) {
                    if (initial.isLetter())
                        binder.UserProfile.setImageDrawable(TextDrawable.builder()
                                .buildRound(initial.toString(), ColorGenerator.MATERIAL.getColor(sender)))
                } else
                    binder.UserProfile.setImageDrawable(ResourcesCompat.getDrawable(itemView.context.resources, R.drawable.ic_user, null))
            } else
                binder.UserProfile.setImageURI(Uri.parse(message.picture))
            binder.txtMessageBody.text = message.body
            binder.txtTime.text = Core.interactiveTime(message.timestamp, itemView.context)
            itemView.setOnClickListener { selectedMessage.onMessageSelected(message, binder.txtSenderName.text.toString()) }
        }
    }
}