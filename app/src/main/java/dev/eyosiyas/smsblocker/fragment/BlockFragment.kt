package dev.eyosiyas.smsblocker.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.BlacklistAdapter
import dev.eyosiyas.smsblocker.database.DatabaseManager

class BlockFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_block, container, false)
        recyclerView = view.findViewById(R.id.blacklistRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadBlacklist()
        val button: FloatingActionButton = view.findViewById(R.id.fabSendMessage)
        button.setOnClickListener { insertUI() }
        return view
    }

    private fun loadBlacklist() {
        val manager: DatabaseManager = DatabaseManager(context)
        val adapter: BlacklistAdapter = BlacklistAdapter(context, manager.blacklist.toMutableList())
        recyclerView.adapter = adapter
    }

    private fun insertUI() {
        val view: View = View.inflate(context, R.layout.blacklist_managment, null)
        val dialog: AlertDialog = AlertDialog.Builder((context)!!).create()
        val number: EditText = view.findViewById(R.id.inputBlacklistNumber)
        val operation: Button = view.findViewById(R.id.btnInsertUpdate)
        val contact: Button = view.findViewById(R.id.btnSelectNumber)
        val manager: DatabaseManager = DatabaseManager(context)
        contact.setOnClickListener {
            Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            //                if (Core.checkContactsPermission(activity))
//                    activity.startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_SELECT_CONTACT);
//                else
//                    ActivityCompat.requestPermissions(activity, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
        dialog.setView(view)
        operation.setOnClickListener {
            manager.insert(number.text.toString())
            Toast.makeText(context, number.text.toString() + " added to blacklist.", Toast.LENGTH_SHORT).show()
            loadBlacklist()
            dialog.dismiss()
        }
        number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                operation.isEnabled = number.text.length > 2
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialog.show()
    }
}