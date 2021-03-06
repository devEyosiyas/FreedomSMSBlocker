package dev.eyosiyas.smsblocker.fragment

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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
import java.util.*

class SettingFragment : Fragment(), KeywordSelected {
    private lateinit var viewModel: KeywordViewModel
    private lateinit var binder: FragmentSettingBinding

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
        viewModel = ViewModelProvider(this).get(KeywordViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = FragmentSettingBinding.bind(inflater.inflate(R.layout.fragment_setting, container, false))
        val manager = PrefManager(requireContext())
        val storedBlockingRule = manager.blockingRule
        val activeRule = when (storedBlockingRule) {
            Constant.STARTS_WITH_TAG -> getString(R.string.starts_with)
            Constant.ENDS_WITH_TAG -> getString(R.string.ends_with)
            Constant.NUCLEAR_OPTION_TAG -> getString(R.string.nuclear_option)
            else -> getString(R.string.no_blocking_rule_is_selected)
        }

        when (storedBlockingRule) {
            Constant.STARTS_WITH_TAG -> {
                binder.radioStartsWith.isChecked = true
                binder.specialRule.visibility = View.VISIBLE
                binder.specialRule.setText(manager.startsWith)
            }
            Constant.ENDS_WITH_TAG -> {
                binder.radioEndsWith.isChecked = true
                binder.specialRule.visibility = View.VISIBLE
                binder.specialRule.setText(manager.endsWith)
            }
            Constant.NUCLEAR_OPTION_TAG -> binder.radioNuclearOption.isChecked = true
        }

        binder.blockingRule.text = String.format(Locale.ENGLISH, getString(R.string.active_blocking_rule), activeRule)
        var tag = ""
        binder.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            val rb = requireActivity().findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            tag = rb.tag.toString()

            when (tag) {
                Constant.STARTS_WITH_TAG -> {
                    binder.specialRule.visibility = View.VISIBLE
                    binder.specialRule.setText(manager.startsWith)
                }
                Constant.ENDS_WITH_TAG -> {
                    binder.specialRule.visibility = View.VISIBLE
                    binder.specialRule.setText(manager.endsWith)
                }
                else -> binder.specialRule.visibility = View.GONE
            }
        }

        binder.btnApply.setOnClickListener {
            if (tag != "") {
                if (tag == Constant.STARTS_WITH_TAG || tag == Constant.ENDS_WITH_TAG) {
                    when {
                        binder.specialRule.text.toString().isEmpty() -> Toast.makeText(requireContext(), getString(R.string.no_number_inserted), Toast.LENGTH_SHORT).show()
                        binder.specialRule.text.toString().length < 2 -> Toast.makeText(requireContext(), getString(R.string.special_blocking_length), Toast.LENGTH_SHORT).show()
                        else -> {
                            if (tag == Constant.STARTS_WITH_TAG) {
                                manager.startsWith = binder.specialRule.text.toString()
                                Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.active_blocking_rule), getString(R.string.starts_with)), Toast.LENGTH_SHORT).show()
                            } else if (tag == Constant.ENDS_WITH_TAG) {
                                manager.endsWith = binder.specialRule.text.toString()
                                Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.active_blocking_rule), getString(R.string.ends_with)), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.active_blocking_rule), getString(R.string.nuclear_option)), Toast.LENGTH_SHORT).show()
                manager.blockingRule = tag
            }
        }
        binder.btnKeyword.setOnClickListener { insertKeywordUI() }
        return binder.root
    }

    private fun insertKeywordUI() {
        val keywordBinder: KeywordManagementBinding = KeywordManagementBinding.bind(View.inflate(requireContext(), R.layout.keyword_management, null))
        val dialog: AlertDialog = AlertDialog.Builder((requireContext())).create()
        keywordBinder.editKeyword.inputType = InputType.TYPE_CLASS_TEXT
        keywordBinder.editKeyword.filters = arrayOf(InputFilter { charSequence, _, _, _, _, _ ->
            Core.limit(charSequence)
        }, Constant.INPUT_LIMIT)
        keywordBinder.btnInsertUpdateKeyword.setOnClickListener {
            if (keywordBinder.editKeyword.text.toString().trim().length > 2) {
                GlobalScope.launch(Dispatchers.Main) {
                    if (viewModel.exists(keywordBinder.editKeyword.text.toString()))
                        Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.keyword_already_exists), keywordBinder.editKeyword.text), Toast.LENGTH_SHORT).show()
                    else {
                        val keyword = Keyword(0, keywordBinder.editKeyword.text.toString())
                        viewModel.addKeyword(keyword)
                        dialog.dismiss()
                        Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.saved_to_db), keywordBinder.editKeyword.text.toString()), Toast.LENGTH_SHORT).show()
                    }
                }
            } else Toast.makeText(requireContext(), getString(R.string.keyword_length_message), Toast.LENGTH_SHORT).show()
        }
        val adapter = KeywordAdapter(this)
        keywordBinder.keywordRecyclerView.adapter = adapter
        keywordBinder.keywordRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.keywords.observe(viewLifecycleOwner, { keyword ->
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
                .setTitle(String.format(Locale.ENGLISH, getString(R.string.remove_keyword_title), keyword.keyword))
                .setCancelable(false)
                .setMessage(String.format(Locale.ENGLISH, getString(R.string.remove_keyword_message), keyword.keyword))
                .setPositiveButton(R.string.button_yes) { _, _ ->
                    viewModel.deleteKeyword(keyword)
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.removed_successfully), keyword.keyword), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.button_no) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun updateUI(keyword: Keyword) {
        val keywordBinder: KeywordManagementBinding = KeywordManagementBinding.bind(View.inflate(requireContext(), R.layout.keyword_management, null))
        keywordBinder.btnInsertUpdateKeyword.text = getString(R.string.update)
        keywordBinder.editKeyword.setText(keyword.keyword)
        keywordBinder.editKeyword.inputType = InputType.TYPE_CLASS_TEXT
        keywordBinder.editKeyword.filters = arrayOf(InputFilter { charSequence, _, _, _, _, _ ->
            Core.limit(charSequence)
        }, Constant.INPUT_LIMIT)
        val updateDialog = AlertDialog.Builder(requireContext()).setView(keywordBinder.root).show()
        keywordBinder.btnInsertUpdateKeyword.setOnClickListener {
            if (keywordBinder.editKeyword.text.toString().trim().length > 2) {
                GlobalScope.launch(Dispatchers.Main) {
                    if (viewModel.exists(keywordBinder.editKeyword.text.toString()))
                        Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.keyword_already_exists), keywordBinder.editKeyword.text), Toast.LENGTH_SHORT).show()
                    else {
                        viewModel.updateKeyword(Keyword(keyword.id, keywordBinder.editKeyword.text.toString()))
                        Toast.makeText(requireContext(), getString(R.string.update_successful), Toast.LENGTH_SHORT).show()
                        updateDialog.dismiss()
                    }
                }
            } else Toast.makeText(requireContext(), R.string.keyword_length_message, Toast.LENGTH_SHORT).show()
        }

    }
}