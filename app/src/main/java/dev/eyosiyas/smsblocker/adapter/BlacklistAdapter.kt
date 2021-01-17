package dev.eyosiyas.smsblocker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.BlacklistItemBinding
import dev.eyosiyas.smsblocker.event.BlacklistSelected
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.util.Core

class BlacklistAdapter(private val selectedBlacklist: BlacklistSelected) : RecyclerView.Adapter<BlacklistAdapter.ViewHolder>() {
    private var blacklists = emptyList<Blacklist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.blacklist_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blacklist = blacklists[position]
        holder.binding.txtBlacklistNumber.text = blacklist.number
        holder.binding.txtBlacklistTimestamp.text = Core.readableTime(blacklist.timestamp, holder.itemView.context)
        holder.binding.imgDeleteNumber.setOnClickListener { selectedBlacklist.onDeleteSelected(blacklist) }
        holder.binding.imgEditNumber.setOnClickListener { selectedBlacklist.onUpdateSelected(blacklist) }
        if (!blacklist.shared && blacklist.number.length < 5)
            holder.binding.imgShareNumber.visibility = View.VISIBLE
        else
            holder.binding.imgShareNumber.visibility = View.INVISIBLE
        holder.binding.imgShareNumber.setOnClickListener { selectedBlacklist.onShareSelected(blacklist) }
    }


    override fun getItemCount(): Int {
        return blacklists.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: BlacklistItemBinding = BlacklistItemBinding.bind(itemView)
    }

    fun populate(blacklists: List<Blacklist>) {
        this.blacklists = blacklists
        notifyDataSetChanged()
    }
}