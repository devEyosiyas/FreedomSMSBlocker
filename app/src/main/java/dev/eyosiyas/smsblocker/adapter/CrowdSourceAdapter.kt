package dev.eyosiyas.smsblocker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.CrowdSourceItemBinding
import dev.eyosiyas.smsblocker.event.CrowdSourceSelected
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.util.Core

class CrowdSourceAdapter(private val crowdSourceSelected: CrowdSourceSelected) : RecyclerView.Adapter<CrowdSourceAdapter.ViewHolder>() {
    private var crowdSourcedBlacklist = emptyList<Blacklist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.crowd_source_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blacklist = crowdSourcedBlacklist[position]
        holder.binding.crowdSourcedBlacklist.text = blacklist.number
        holder.binding.crowdSourcedTimestamp.text = Core.readableTime(blacklist.timestamp, holder.itemView.context)
        holder.binding.addCrowdSourcedBlacklist.setOnClickListener { crowdSourceSelected.onInsertSelected(blacklist) }
    }


    override fun getItemCount(): Int {
        return crowdSourcedBlacklist.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: CrowdSourceItemBinding = CrowdSourceItemBinding.bind(itemView)
    }

    fun populate(crowdSourcedBlacklist: List<Blacklist>) {
        this.crowdSourcedBlacklist = crowdSourcedBlacklist
        notifyDataSetChanged()
    }
}