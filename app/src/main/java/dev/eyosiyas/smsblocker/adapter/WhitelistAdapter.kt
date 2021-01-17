package dev.eyosiyas.smsblocker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.BlacklistItemBinding
import dev.eyosiyas.smsblocker.event.WhitelistSelected
import dev.eyosiyas.smsblocker.model.Whitelist
import dev.eyosiyas.smsblocker.util.Core

class WhitelistAdapter(private val selectedWhitelist: WhitelistSelected) : RecyclerView.Adapter<WhitelistAdapter.ViewHolder>() {
    private var whitelists = emptyList<Whitelist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.blacklist_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val whitelist = whitelists[position]
        holder.binding.txtBlacklistNumber.text = whitelist.number
        holder.binding.txtBlacklistTimestamp.text = Core.readableTime(whitelist.timestamp, holder.itemView.context)
        holder.binding.imgDeleteNumber.setOnClickListener { selectedWhitelist.onDeleteSelected(whitelist) }
        holder.binding.imgEditNumber.setOnClickListener { selectedWhitelist.onUpdateSelected(whitelist) }
    }


    override fun getItemCount(): Int {
        return whitelists.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: BlacklistItemBinding = BlacklistItemBinding.bind(itemView)
    }

    fun populate(whitelists: List<Whitelist>) {
        this.whitelists = whitelists
        notifyDataSetChanged()
    }
}