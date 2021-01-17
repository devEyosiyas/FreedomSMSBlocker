package dev.eyosiyas.smsblocker.fragment

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.WhitelistAdapter
import dev.eyosiyas.smsblocker.databinding.BlacklistManagmentBinding
import dev.eyosiyas.smsblocker.databinding.FragmentWhitelistBinding
import dev.eyosiyas.smsblocker.event.WhitelistSelected
import dev.eyosiyas.smsblocker.model.Whitelist
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.viewmodel.WhitelistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class WhitelistFragment : Fragment(), WhitelistSelected {
    private lateinit var viewModel: WhitelistViewModel
    private lateinit var binder: FragmentWhitelistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = Locale(PrefManager(requireContext()).locale)
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else
            configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            requireContext().createConfigurationContext(configuration)
        else
            resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binder = FragmentWhitelistBinding.bind(inflater.inflate(R.layout.fragment_whitelist, container, false))
        binder.whitelistRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this).get(WhitelistViewModel::class.java)
        val adapter = WhitelistAdapter(this)
        binder.whitelistRecycler.adapter = adapter
        viewModel.whitelists.observe(viewLifecycleOwner, { whitelist ->
            adapter.populate(whitelist)
        })
        binder.fabAddWhitelist.setOnClickListener { insertUI() }
        return binder.root
    }

    private fun deleteUI(whitelist: Whitelist) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.remove_whitelist_title))
                .setCancelable(false)
                .setMessage(String.format(Locale.ENGLISH, getString(R.string.remove_whitelist_message), whitelist.number))
                .setPositiveButton(getString(R.string.button_yes)) { _, _ ->
                    viewModel.deleteWhitelist(whitelist)
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.removed_successfully), whitelist.number), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.button_no)) { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun insertUI() {
        val blacklistBinder: BlacklistManagmentBinding = BlacklistManagmentBinding.bind(View.inflate(context, R.layout.blacklist_managment, null))
        val insertDialog: AlertDialog = AlertDialog.Builder((requireContext())).create()
        blacklistBinder.btnSelectNumber.visibility = View.GONE
        blacklistBinder.inputBlacklistNumber.filters = arrayOf(Constant.INPUT_LIMIT_WHITELIST)
        insertDialog.setView(blacklistBinder.root)
        blacklistBinder.btnInsertUpdate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (viewModel.exists(blacklistBinder.inputBlacklistNumber.text.toString()))
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.already_whitelisted), blacklistBinder.inputBlacklistNumber.text), Toast.LENGTH_SHORT).show()
                else {
                    viewModel.addWhitelist(Whitelist(0, blacklistBinder.inputBlacklistNumber.text.toString(), System.currentTimeMillis()))
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.saved_to_db), blacklistBinder.inputBlacklistNumber.text), Toast.LENGTH_SHORT).show()
                    insertDialog.dismiss()
                }
            }
        }
        blacklistBinder.inputBlacklistNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                blacklistBinder.btnInsertUpdate.isEnabled = blacklistBinder.inputBlacklistNumber.text.length > 2
            }

            override fun afterTextChanged(s: Editable) {}
        })
        insertDialog.show()
    }

    private fun updateUI(whitelist: Whitelist) {
        val binder: BlacklistManagmentBinding = BlacklistManagmentBinding.bind(View.inflate(requireContext(), R.layout.blacklist_managment, null))
        val updateDialog = AlertDialog.Builder(requireContext()).create()
        binder.btnInsertUpdate.setText(R.string.update)
        binder.inputBlacklistNumber.setText(whitelist.number)
        binder.inputBlacklistNumber.filters = arrayOf(InputFilter { charSequence, _, _, _, _, _ ->
            Core.limit(charSequence)
        }, Constant.INPUT_LIMIT_WHITELIST)
        binder.btnSelectNumber.visibility = View.GONE
        updateDialog.setView(binder.root)
        binder.btnInsertUpdate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (viewModel.exists(binder.inputBlacklistNumber.text.toString()))
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.already_whitelisted), binder.inputBlacklistNumber.text), Toast.LENGTH_SHORT).show()
                else {
                    viewModel.updateWhitelist(Whitelist(whitelist.id, binder.inputBlacklistNumber.text.toString(), System.currentTimeMillis()))
                    Toast.makeText(requireContext(), getString(R.string.whitelist_record_updated), Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
                }
            }

        }
        binder.inputBlacklistNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binder.btnInsertUpdate.isEnabled = binder.inputBlacklistNumber.text.length > 2
            }

            override fun afterTextChanged(s: Editable) {}
        })
        updateDialog.show()
    }

    override fun onUpdateSelected(whitelist: Whitelist) {
        updateUI(whitelist)
    }

    override fun onDeleteSelected(whitelist: Whitelist) {
        deleteUI(whitelist)
    }
}