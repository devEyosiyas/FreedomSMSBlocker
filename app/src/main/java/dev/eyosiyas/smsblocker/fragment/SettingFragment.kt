package dev.eyosiyas.smsblocker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.KeywordAdapter
import dev.eyosiyas.smsblocker.database.DatabaseManager
import dev.eyosiyas.smsblocker.util.PrefManager

class SettingFragment : Fragment() {
    private lateinit var databaseManager: DatabaseManager
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseManager = DatabaseManager(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_setting, container, false)
        val startsWith: EditText = view.findViewById(R.id.editStartsWith)
        val endsWith: EditText = view.findViewById(R.id.editEndsWith)
        val keyword: Button = view.findViewById(R.id.btnKeyword)
        val changeRule: Button = view.findViewById(R.id.btnChangeRule)
        val nuclearSwitch: SwitchCompat = view.findViewById(R.id.nuclearOption)
        val manager: PrefManager = PrefManager(context)
        nuclearSwitch.isChecked = manager.isNuclearEnabled
        startsWith.setText(manager.startsWith)
        endsWith.setText(manager.endsWith)
        changeRule.setOnClickListener {
            if (startsWith.text.isNotEmpty()) {
                manager.startsWith = startsWith.text.toString()
                Toast.makeText(context, String.format("Now blocking every number that starts with %s", startsWith.text.toString()), Toast.LENGTH_LONG).show()
            } else {
                manager.startsWith = ""
                Toast.makeText(context, "Starts with blocking disabled ", Toast.LENGTH_LONG).show()
            }
            if (endsWith.text.isNotEmpty()) {
                manager.endsWith = endsWith.text.toString()
                Toast.makeText(context, String.format("Now blocking every number that ends with %s", endsWith.text.toString()), Toast.LENGTH_LONG).show()
            } else {
                manager.endsWith = ""
                Toast.makeText(context, "Ends with blocking disabled ", Toast.LENGTH_LONG).show()
            }
        }
        nuclearSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                manager.setNuclear(true)
                Toast.makeText(context, "Nuclear option enabled!\nNow blocking EVERY 3 & 4 digit numbers.", Toast.LENGTH_LONG).show()
            } else manager.setNuclear(false)
        }
        keyword.setOnClickListener { insertKeywordUI() }
        return view
    }

    private fun insertKeywordUI() {
        val view: View = View.inflate(context, R.layout.keyword_management, null)
        val dialog: AlertDialog = AlertDialog.Builder((context)!!).create()
        val databaseManager: DatabaseManager = DatabaseManager(context)
        val editKeyword: EditText = view.findViewById(R.id.editKeyword)
        val insert: Button = view.findViewById(R.id.btnInsertUpdateKeyword)
        recyclerView = view.findViewById(R.id.keywordRecyclerView)
        insert.setOnClickListener {
            if (editKeyword.text.toString().length > 2) {
                databaseManager.insertKeyword(editKeyword.text.toString())
                Toast.makeText(context, editKeyword.text.toString() + " saved to the database.", Toast.LENGTH_SHORT).show()
                editKeyword.setText("")
                refreshKeyword()
            } else Toast.makeText(context, "Please use a keyword of length more than three.", Toast.LENGTH_SHORT).show()
        }
        refreshKeyword()
        recyclerView.layoutManager = LinearLayoutManager(context)
        dialog.setView(view)
        dialog.show()
    }

    private fun refreshKeyword() {
        val keywordAdapter: KeywordAdapter = KeywordAdapter(databaseManager.keywords.toMutableList(), context!!)
        recyclerView.adapter = keywordAdapter
    }
}