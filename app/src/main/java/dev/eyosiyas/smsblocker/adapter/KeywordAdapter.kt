package dev.eyosiyas.smsblocker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.KeywordAdapter.VH
import dev.eyosiyas.smsblocker.databinding.KeywordItemBinding
import dev.eyosiyas.smsblocker.event.KeywordSelected
import dev.eyosiyas.smsblocker.model.Keyword


class KeywordAdapter(private val selectedKeyword: KeywordSelected) : RecyclerView.Adapter<VH>() {
    private var keywords = emptyList<Keyword>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.keyword_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val keyword = keywords[position]
        holder.binder.txtKeyword.text = keyword.keyword
        holder.binder.deleteKeyword.setOnClickListener {
            selectedKeyword.onDeleteSelected(keyword)
        }
        holder.binder.editKeyword.setOnClickListener {
            selectedKeyword.onUpdateSelected(keywords[position])
        }
    }

    override fun getItemCount(): Int {
        return keywords.size
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binder: KeywordItemBinding = KeywordItemBinding.bind(itemView)
    }

    fun data(keywords: List<Keyword>) {
        this.keywords = keywords
        notifyDataSetChanged()
    }
}