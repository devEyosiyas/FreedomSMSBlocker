package dev.eyosiyas.smsblocker.fragment

import android.os.Bundle
import android.text.Editable
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
import dev.eyosiyas.smsblocker.adapter.BlacklistAdapter
import dev.eyosiyas.smsblocker.databinding.BlacklistManagmentBinding
import dev.eyosiyas.smsblocker.databinding.FragmentBlockBinding
import dev.eyosiyas.smsblocker.event.BlacklistSelected
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.viewmodel.BlacklistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BlockFragment : Fragment(), BlacklistSelected {
    private lateinit var viewModel: BlacklistViewModel
    private lateinit var binder: FragmentBlockBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binder = FragmentBlockBinding.bind(inflater.inflate(R.layout.fragment_block, container, false))
        binder.blacklistRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this).get(BlacklistViewModel::class.java)
        val adapter = BlacklistAdapter(this)
        binder.blacklistRecycler.adapter = adapter
        viewModel.blacklists.observe(viewLifecycleOwner, { blacklist ->
            adapter.populate(blacklist)
        })
//        loadBlacklist()
        binder.fabSendMessage.setOnClickListener { insertUI() }
        return binder.root
    }

    override fun onUpdateSelected(blacklist: Blacklist) {
        updateUI(blacklist)
    }

    override fun onDeleteSelected(blacklist: Blacklist) {
        deleteUI(blacklist)
    }

    private fun deleteUI(blacklist: Blacklist) {
        AlertDialog.Builder(requireContext())
                .setTitle("Remove from Blacklist.")
                .setCancelable(false)
                .setMessage("Do you want to remove ${blacklist.number} from the Blacklist?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteBlacklist(blacklist)
                    Toast.makeText(requireContext(), "${blacklist.number} removed successfully.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun insertUI() {
        val blacklistBinder: BlacklistManagmentBinding = BlacklistManagmentBinding.bind(View.inflate(context, R.layout.blacklist_managment, null))
        val insertDialog: AlertDialog = AlertDialog.Builder((requireContext())).create()
        blacklistBinder.btnSelectNumber.setOnClickListener {
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
            //                if (Core.checkContactsPermission(activity))
//                    activity.startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_SELECT_CONTACT);
//                else
//                    ActivityCompat.requestPermissions(activity, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
        insertDialog.setView(blacklistBinder.root)
        blacklistBinder.btnInsertUpdate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (viewModel.exists(blacklistBinder.inputBlacklistNumber.text.toString()))
                    Toast.makeText(requireContext(), "${blacklistBinder.inputBlacklistNumber.text} already blocked.", Toast.LENGTH_SHORT).show()
                else {
                    viewModel.addBlacklist(Blacklist(0, blacklistBinder.inputBlacklistNumber.text.toString(), System.currentTimeMillis()))
                    Toast.makeText(requireContext(), "${blacklistBinder.inputBlacklistNumber.text} saved to the database.", Toast.LENGTH_SHORT).show()
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

    private fun updateUI(blacklist: Blacklist) {
        val binder: BlacklistManagmentBinding = BlacklistManagmentBinding.bind(View.inflate(requireContext(), R.layout.blacklist_managment, null))
        val updateDialog = AlertDialog.Builder(requireContext()).create()
        binder.btnInsertUpdate.setText(R.string.update)
        binder.inputBlacklistNumber.setText(blacklist.number)
        binder.btnSelectNumber.setOnClickListener {
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
            //                if (Core.checkContactsPermission(activity))
//                    activity.startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_SELECT_CONTACT);
//                else
//                    ActivityCompat.requestPermissions(activity, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
        updateDialog.setView(binder.root)
        binder.btnInsertUpdate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (viewModel.exists(binder.inputBlacklistNumber.text.toString()))
                    Toast.makeText(requireContext(), "${binder.inputBlacklistNumber.text} already blocked.", Toast.LENGTH_SHORT).show()
                else {
                    viewModel.updateBlacklist(Blacklist(blacklist.id, binder.inputBlacklistNumber.text.toString(), System.currentTimeMillis()))
                    Toast.makeText(requireContext(), "Blacklist record updated.", Toast.LENGTH_SHORT).show()
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
}