package dev.eyosiyas.smsblocker.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.KeywordAdapter
import dev.eyosiyas.smsblocker.databinding.FragmentSettingBinding
import dev.eyosiyas.smsblocker.databinding.KeywordManagementBinding
import dev.eyosiyas.smsblocker.event.KeywordSelected
import dev.eyosiyas.smsblocker.model.Keyword
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.viewmodel.KeywordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingFragment : Fragment(), KeywordSelected {
    private lateinit var viewModel: KeywordViewModel
    private lateinit var binder: FragmentSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KeywordViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = FragmentSettingBinding.bind(inflater.inflate(R.layout.fragment_setting, container, false))
        val manager = PrefManager(requireContext())
        binder.nuclearOption.isChecked = manager.nuclearOption
//        binder.editStartsWith.setText(manager.startsWith)
//        binder.editEndsWith.setText(manager.endsWith)
        binder.btnApply.setOnClickListener {
//            if (binder.editStartsWith.text.isNotEmpty()) {
//                manager.startsWith = binder.editStartsWith.text.toString()
//                Toast.makeText(context, String.format("Now blocking every number that starts with %s", binder.editStartsWith.text.toString()), Toast.LENGTH_LONG).show()
//            } else {
//                manager.startsWith = ""
//                Toast.makeText(context, "Starts with blocking disabled ", Toast.LENGTH_LONG).show()
//            }
//            if (binder.editEndsWith.text.isNotEmpty()) {
//                manager.endsWith = binder.editEndsWith.text.toString()
//                Toast.makeText(context, String.format("Now blocking every number that ends with %s", binder.editEndsWith.text.toString()), Toast.LENGTH_LONG).show()
//            } else {
//                manager.endsWith = ""
//                Toast.makeText(context, "Ends with blocking disabled ", Toast.LENGTH_LONG).show()
//            }
        }
        binder.nuclearOption.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                manager.nuclearOption = true
                Toast.makeText(context, "Nuclear option enabled!\nNow blocking EVERY 3 & 4 digit numbers.", Toast.LENGTH_LONG).show()
            } else manager.nuclearOption = false
        }
        binder.btnKeyword.setOnClickListener { insertKeywordUI() }
        return binder.root
    }

    private fun insertKeywordUI() {
        val keywordBinder: KeywordManagementBinding = KeywordManagementBinding.bind(View.inflate(context, R.layout.keyword_management, null))
        val dialog: AlertDialog = AlertDialog.Builder((context)!!).create()
        keywordBinder.editKeyword.inputType = InputType.TYPE_CLASS_TEXT
        keywordBinder.editKeyword.filters = arrayOf(InputFilter { charSequence, _, _, _, _, _ ->
            Core.limit(charSequence)
        }, Constant.INPUT_LIMIT)
        keywordBinder.btnInsertUpdateKeyword.setOnClickListener {
            if (keywordBinder.editKeyword.text.toString().trim().length > 2) {
//                if (databaseManager.keywordExist(keywordBinder.editKeyword.text.toString()))
                GlobalScope.launch(Dispatchers.Main) {
                    if (viewModel.exists(keywordBinder.editKeyword.text.toString()))
                        Toast.makeText(context, "${keywordBinder.editKeyword.text} already exists.", Toast.LENGTH_SHORT).show()
                    else {
                        val keyword = Keyword(0, keywordBinder.editKeyword.text.toString())
                        viewModel.addKeyword(keyword)
                        Toast.makeText(context, keywordBinder.editKeyword.text.toString() + " saved to the database.", Toast.LENGTH_SHORT).show()
                        keywordBinder.editKeyword.setText("")
                    }
                }
            } else Toast.makeText(context, "Please use a keyword of length three to fifteen.", Toast.LENGTH_SHORT).show()
        }
        val adapter = KeywordAdapter(this)
        keywordBinder.keywordRecyclerView.adapter = adapter
        keywordBinder.keywordRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.keywords.observe(viewLifecycleOwner, Observer { keyword ->
            adapter.data(keyword)
        })
        dialog.setView(keywordBinder.root)
        dialog.show()
    }

    override fun onInsertSelected(keyword: Keyword) {
        // TODO: 1/12/2021 differentiate the UI between Update and insert
        updateUI(keyword)
    }

    override fun onUpdateSelected(keyword: Keyword) {
        // TODO: 1/12/2021 differentiate the UI between Update and insert
        updateUI(keyword)
    }

    override fun onDeleteSelected(keyword: Keyword) {
        deleteUI(keyword)
    }

    private fun deleteUI(keyword: Keyword) {
        AlertDialog.Builder(requireContext())
                .setTitle("Remove ${keyword.keyword}")
                .setCancelable(false)
                .setMessage("Are you sure you want to remove ${keyword.keyword}?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteKeyword(keyword)
                    Toast.makeText(context, "${keyword.keyword} removed successfully.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun updateUI(keyword: Keyword) {
        val keywordBinder: KeywordManagementBinding = KeywordManagementBinding.bind(View.inflate(context, R.layout.keyword_management, null))
        keywordBinder.btnInsertUpdateKeyword.text = getString(R.string.update)
        keywordBinder.editKeyword.setText(keyword.keyword)
        keywordBinder.editKeyword.inputType = InputType.TYPE_CLASS_TEXT
        keywordBinder.editKeyword.filters = arrayOf(InputFilter { charSequence, _, _, _, _, _ ->
            Core.limit(charSequence)
        }, Constant.INPUT_LIMIT)
        keywordBinder.btnInsertUpdateKeyword.setOnClickListener {
            if (keywordBinder.editKeyword.text.toString().trim().length > 2) {
                GlobalScope.launch(Dispatchers.Main) {
                    if (viewModel.exists(keywordBinder.editKeyword.text.toString()))
                        Toast.makeText(context, "${keywordBinder.editKeyword.text} already exists.", Toast.LENGTH_SHORT).show()
                    else {
                        viewModel.updateKeyword(Keyword(keyword.id, keywordBinder.editKeyword.text.toString()))
                        Toast.makeText(context, "Update was successful!", Toast.LENGTH_SHORT).show()
                        keywordBinder.editKeyword.setText("")
                    }
                }
            } else Toast.makeText(context, "Please use a keyword of length three to fifteen.", Toast.LENGTH_SHORT).show()
        }
        AlertDialog.Builder(requireContext()).setView(keywordBinder.root).show()
    }
}