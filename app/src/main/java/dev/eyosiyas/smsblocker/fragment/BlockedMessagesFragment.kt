package dev.eyosiyas.smsblocker.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.BlockedAdapter
import dev.eyosiyas.smsblocker.databinding.FragmentBlockedMessagesBinding
import dev.eyosiyas.smsblocker.event.BlockedMessageSelected
import dev.eyosiyas.smsblocker.model.Blocked
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.view.DetailSmsActivity
import dev.eyosiyas.smsblocker.viewmodel.BlockedViewModel
import java.util.*

class BlockedMessagesFragment : Fragment(), BlockedMessageSelected {
    private lateinit var viewModel: BlockedViewModel
    private lateinit var binder: FragmentBlockedMessagesBinding


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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = FragmentBlockedMessagesBinding.bind(inflater.inflate(R.layout.fragment_blocked_messages, container, false))
        binder.blockedMessageRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this).get(BlockedViewModel::class.java)
        val adapter = BlockedAdapter(this)
        binder.blockedMessageRecycler.adapter = adapter
        viewModel.blockedMessages.observe(viewLifecycleOwner, { blockedMessages ->
            adapter.populate(blockedMessages)
        })
        return binder.root
    }

    override fun onSelected(blockedMessage: Blocked) {
        startActivity(Intent(requireContext(), DetailSmsActivity::class.java).putExtra("KEY", blockedMessage.sender).putExtra("DISPLAY", blockedMessage.sender))
    }

    override fun onDeleteSelected(blockedMessage: Blocked) {
        deleteBlockedMessage(blockedMessage)
    }

    private fun deleteBlockedMessage(blockedMessage: Blocked) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.message_delete_title))
                .setCancelable(false)
                .setMessage(getString(R.string.message_delete_body))
                .setPositiveButton(R.string.button_yes) { _, _ ->
                    viewModel.deleteBlocked(blockedMessage)
                    Toast.makeText(requireContext(), getString(R.string.message_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.button_no) { dialog, _ -> dialog.dismiss() }
                .show()
    }
}