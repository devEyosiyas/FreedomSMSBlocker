package dev.eyosiyas.smsblocker.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.view.DetailSmsActivity

class MessageAdapter(private val context: Context?, private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.sender != message.displayName) holder.sender.text = message.displayName else holder.sender.text = message.sender
        holder.body.text = message.body
        holder.time.text = Core.readableTime(message.timestamp)
        holder.itemView.setOnClickListener(View.OnClickListener { context!!.startActivity(Intent(context, DetailSmsActivity::class.java).putExtra("KEY", message.sender).putExtra("DISPLAY", holder.sender.text.toString())) })
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: ImageView
        var sender: TextView
        var body: TextView
        var time: TextView

        init {
            profile = itemView.findViewById(R.id.UserProfile)
            sender = itemView.findViewById(R.id.txtSenderName)
            body = itemView.findViewById(R.id.txtMessageBody)
            time = itemView.findViewById(R.id.txtTime)
        }
    }
}