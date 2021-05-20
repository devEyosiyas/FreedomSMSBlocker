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
import java.util.*

class CrowdSourceAdapter(private val crowdSourceSelected: CrowdSourceSelected) : RecyclerView.Adapter<CrowdSourceAdapter.ViewHolder>() {
    private var crowdSourcedBlacklist = emptyList<Blacklist>()
    private var selected = ArrayList<Blacklist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.crowd_source_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blacklist = crowdSourcedBlacklist[position]
        holder.binding.crowdSourcedBlacklist.text = blacklist.number
        holder.binding.crowdSourcedTimestamp.text = Core.readableTime(blacklist.timestamp, holder.itemView.context)
//        holder.binding.addCrowdSourcedBlacklist.setOnClickListener { crowdSourceSelected.onInsertSelected(blacklist) }

        holder.binding.crowdSourceCard.isChecked = blacklist.checked
        holder.binding.crowdSourceCard.setOnLongClickListener {
            crowdSourceSelected.onLongPress(position)
//            holder.binding.crowdSourceCard.isChecked = !holder.binding.crowdSourceCard.isChecked
//            blacklist.selected = !blacklist.selected
//            checker(blacklist)
//            holder.binding.crowdSourceCard.isSelected = blacklist.selected
//            notifyItemChanged(position)
            true
        }
    }

    fun checker(position: Int) {
        crowdSourcedBlacklist[position].checked = !crowdSourcedBlacklist[position].checked
        if (crowdSourcedBlacklist[position].checked)
            selected.add(crowdSourcedBlacklist[position])
        else
            selected.remove(crowdSourcedBlacklist[position])
        notifyItemChanged(position)
    }

    fun allSelected(): Boolean {
        return if (selected.size == crowdSourcedBlacklist.size) {
            crowdSourceSelected.onActivate(true)
            true
        } else {
            crowdSourceSelected.onActivate(false)
            false
        }
    }

    fun selectAll(selectAll: Boolean) {
        if (selectAll) {
            selected.clear()
            for (blacklist: Blacklist in crowdSourcedBlacklist) {
                blacklist.checked = true
                selected.add(blacklist)
            }
        } else {
            for (blacklist: Blacklist in crowdSourcedBlacklist) {
                blacklist.checked = false
                selected.remove(blacklist)
            }
        }
        notifyDataSetChanged()
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

    companion object {
        private const val TAG = "CrowdSourceAdapter"
    }
}