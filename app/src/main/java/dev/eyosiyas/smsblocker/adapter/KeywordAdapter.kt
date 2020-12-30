package dev.eyosiyas.smsblocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.KeywordAdapter.VH
import dev.eyosiyas.smsblocker.database.DatabaseManager
import dev.eyosiyas.smsblocker.model.Keyword
import java.util.*

class KeywordAdapter(private val keywordList: MutableList<Keyword?>, private val context: Context) : RecyclerView.Adapter<VH>() {
    var manager: DatabaseManager = DatabaseManager(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.keyword_item, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val keyword = keywordList[position]
        holder.keyword.text = keyword!!.keyword
        holder.delete.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(String.format("Remove %s", keyword.keyword))
                    .setCancelable(false)
                    .setMessage(String.format(Locale.US, "Are you sure you want to remove %s?", keyword.keyword))
                    .setPositiveButton("Yes") { dialog, which ->
                        manager.removeKeyword(keyword.id)
                        keywordList.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, keyword.keyword + " removed successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            dialog.show()
        }
        holder.edit.setOnClickListener { updateKeywordUI(keyword.id, position) }
    }

    override fun getItemCount(): Int {
        return keywordList.size
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keyword: TextView = itemView.findViewById(R.id.txtKeyword)
        val delete: ImageView = itemView.findViewById(R.id.deleteKeyword)
        val edit: ImageView = itemView.findViewById(R.id.editKeyword)

    }

    private fun updateKeywordUI(id: Int, position: Int) {
        val view = View.inflate(context, R.layout.keyword_management, null)
        val dialog = AlertDialog.Builder(context).create()
        val databaseManager = DatabaseManager(context)
        val editKeyword = view.findViewById<EditText>(R.id.editKeyword)
        val update = view.findViewById<Button>(R.id.btnInsertUpdateKeyword)
        val recyclerView: RecyclerView = view.findViewById(R.id.keywordRecyclerView)
        val keywordAdapter = KeywordAdapter(databaseManager.keywords.toMutableList(), context)
        update.text = context.getString(R.string.update)
        editKeyword.setText(keywordList[position]!!.keyword)
        update.setOnClickListener {
            if (editKeyword.text.toString().length > 2) {
                databaseManager.updateKeyword(id, editKeyword.text.toString())
                Toast.makeText(context, "Update was successful!", Toast.LENGTH_SHORT).show()
                editKeyword.setText("")
                keywordList[position] = manager.getKeyword(id)
                notifyItemChanged(position)
                notifyItemChanged(position, keywordList[position])
            } else Toast.makeText(context, "Please use a keyword of length more than three.", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = keywordAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        dialog.setView(view)
        dialog.show()
    }

}