package dev.eyosiyas.smsblocker.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.database.DatabaseManager
import dev.eyosiyas.smsblocker.databinding.BlacklistItemBinding
import dev.eyosiyas.smsblocker.databinding.BlacklistManagmentBinding
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.util.Core
import java.util.*

class BlacklistAdapter(private val context: Context?, private val blacklists: MutableList<Blacklist?>?) : RecyclerView.Adapter<BlacklistAdapter.ViewHolder>() {
    private val manager: DatabaseManager = DatabaseManager(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blacklist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blacklist = blacklists?.get(position)
        holder.binding.txtBlacklistNumber.text = blacklist!!.number
        holder.binding.txtBlacklistTimestamp.text = Core.readableTime(blacklist.timestamp)
        holder.binding.imgDeleteNumber.setOnClickListener {
            val blacklistDialog = AlertDialog.Builder(context!!)
            blacklistDialog.setTitle("Remove from Blacklist.")
                    .setCancelable(false)
                    .setMessage(String.format(Locale.US, "Do you want to remove %s from the Blacklist?", blacklist.number))
                    .setPositiveButton("Yes") { _, _ ->
                        manager.remove(blacklist.id)
                        blacklists!!.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, blacklist.number + " removed successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            blacklistDialog.show()
        }
        holder.binding.imgEditNumber.setOnClickListener { blacklistManager(blacklist.id, position) }
    }

    private fun blacklistManager(id: Int, position: Int) {
        val view = View.inflate(context, R.layout.blacklist_managment, null)
        val binder: BlacklistManagmentBinding = BlacklistManagmentBinding.bind(view)
        val dialog = AlertDialog.Builder(context!!).create()
//        val number = view.findViewById<EditText>(R.id.inputBlacklistNumber)
//        val operation = view.findViewById<Button>(R.id.btnInsertUpdate)
//        val contact = view.findViewById<Button>(R.id.btnSelectNumber)
        val manager = DatabaseManager(context)
        binder.btnInsertUpdate.setText(R.string.update)
        binder.inputBlacklistNumber.setText(if (id > 0) manager.blacklist(id)?.number else "")
        binder.btnSelectNumber.setOnClickListener {
            Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            //                if (Core.checkContactsPermission(activity))
//                    activity.startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_SELECT_CONTACT);
//                else
//                    ActivityCompat.requestPermissions(activity, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
        dialog.setView(view)
        binder.btnInsertUpdate.setOnClickListener {
            manager.update(id, binder.inputBlacklistNumber.text.toString())
            blacklists!![position] = manager.blacklist(id)
            notifyItemChanged(position)
            Toast.makeText(context, "Blacklist record updated.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        binder.inputBlacklistNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binder.btnInsertUpdate.isEnabled = binder.inputBlacklistNumber.text.length > 2
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialog.show()
    }

    override fun getItemCount(): Int {
        return blacklists!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: BlacklistItemBinding = BlacklistItemBinding.bind(itemView)

//        var delete: ImageView = itemView.findViewById(R.id.imgDeleteNumber)
//        var edit: ImageView = itemView.findViewById(R.id.imgEditNumber)
//        var number: TextView = itemView.findViewById(R.id.txtBlacklistNumber)
//        var time: TextView = itemView.findViewById(R.id.txtBlacklistTimestamp)
    }

}