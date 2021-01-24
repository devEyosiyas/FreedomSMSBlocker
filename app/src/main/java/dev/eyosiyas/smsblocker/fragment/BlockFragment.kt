package dev.eyosiyas.smsblocker.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.BlacklistAdapter
import dev.eyosiyas.smsblocker.databinding.BlacklistManagmentBinding
import dev.eyosiyas.smsblocker.databinding.FragmentBlockBinding
import dev.eyosiyas.smsblocker.event.BlacklistSelected
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.viewmodel.BlacklistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class BlockFragment : Fragment(), BlacklistSelected {
    private lateinit var viewModel: BlacklistViewModel
    private lateinit var binder: FragmentBlockBinding
    private lateinit var blacklistBinder: BlacklistManagmentBinding


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
        binder = FragmentBlockBinding.bind(inflater.inflate(R.layout.fragment_block, container, false))
        binder.blacklistRecycler.layoutManager = LinearLayoutManager(requireContext())
        blacklistBinder = BlacklistManagmentBinding.bind(View.inflate(requireContext(), R.layout.blacklist_managment, null))
        viewModel = ViewModelProvider(this).get(BlacklistViewModel::class.java)
        val adapter = BlacklistAdapter(this)
        binder.blacklistRecycler.adapter = adapter
        viewModel.blacklists.observe(viewLifecycleOwner, { blacklist ->
            adapter.populate(blacklist)
        })
        binder.fabAddBlacklist.setOnClickListener { insertUI() }
        return binder.root
    }

    override fun onUpdateSelected(blacklist: Blacklist) {
        updateUI(blacklist)
    }

    override fun onDeleteSelected(blacklist: Blacklist) {
        deleteUI(blacklist)
    }

    override fun onShareSelected(blacklist: Blacklist) {
        shareUI(blacklist)
    }

    private fun deleteUI(blacklist: Blacklist) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.remove_blacklist_title))
                .setCancelable(false)
                .setMessage(String.format(Locale.ENGLISH, getString(R.string.remove_blacklist_message), blacklist.number))
                .setPositiveButton(getString(R.string.button_yes)) { _, _ ->
                    viewModel.deleteBlacklist(blacklist)
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.removed_successfully), blacklist.number), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.button_no)) { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun insertUI() {
        blacklistBinder = BlacklistManagmentBinding.bind(View.inflate(requireContext(), R.layout.blacklist_managment, null))
        val insertDialog: AlertDialog = AlertDialog.Builder((requireContext())).create()
        blacklistBinder.btnSelectNumber.setOnClickListener { formattedContact() }
        insertDialog.setView(blacklistBinder.root)
        blacklistBinder.btnInsertUpdate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (viewModel.exists(blacklistBinder.inputBlacklistNumber.text.toString()))
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.already_blocked), blacklistBinder.inputBlacklistNumber.text), Toast.LENGTH_SHORT).show()
                else {
                    viewModel.addBlacklist(Blacklist(0, blacklistBinder.inputBlacklistNumber.text.toString(), System.currentTimeMillis(), Constant.SOURCE_LOCAL, false))
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

    private fun updateUI(blacklist: Blacklist) {
        blacklistBinder = BlacklistManagmentBinding.bind(View.inflate(requireContext(), R.layout.blacklist_managment, null))
        val updateDialog = AlertDialog.Builder(requireContext()).create()
        blacklistBinder.btnInsertUpdate.setText(R.string.update)
        blacklistBinder.inputBlacklistNumber.setText(blacklist.number)
        blacklistBinder.btnSelectNumber.setOnClickListener { formattedContact() }
        updateDialog.setView(blacklistBinder.root)
        blacklistBinder.btnInsertUpdate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (viewModel.exists(blacklistBinder.inputBlacklistNumber.text.toString()))
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.already_blocked), blacklistBinder.inputBlacklistNumber.text), Toast.LENGTH_SHORT).show()
                else {
                    viewModel.updateBlacklist(Blacklist(blacklist.id, blacklistBinder.inputBlacklistNumber.text.toString(), System.currentTimeMillis(), Constant.SOURCE_LOCAL, blacklist.shared))
                    Toast.makeText(requireContext(), getString(R.string.blacklist_record_updated), Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
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
        updateDialog.show()
    }

    private fun shareUI(blacklist: Blacklist) {
        val remoteDB = Firebase.firestore
        remoteDB.collection(Constant.PATH_SHORT_CODE).document(blacklist.number)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        viewModel.updateBlacklist(Blacklist(blacklist.id, blacklist.number, blacklist.timestamp, Constant.SOURCE_LOCAL, true))
                        Toast.makeText(requireContext(), getString(R.string.crowdsource_already_contributed), Toast.LENGTH_SHORT).show()
                    } else {
                        AlertDialog.Builder(requireContext())
                                .setTitle(getString(R.string.share_crowdsource_title))
                                .setCancelable(false)
                                .setMessage(String.format(Locale.ENGLISH, getString(R.string.share_crowdsource_message), blacklist.number))
                                .setPositiveButton(getString(R.string.button_yes)) { _, _ ->
                                    remoteDB.collection(Constant.PATH_SHORT_CODE).document(blacklist.number).set(hashMapOf(Constant.FIELD_NUMBER to blacklist.number, Constant.FIELD_TIMESTAMP to System.currentTimeMillis() / 1000, Constant.FIELD_USER_GENERATED to true))
                                            .addOnSuccessListener {
                                                viewModel.updateBlacklist(Blacklist(blacklist.id, blacklist.number, blacklist.timestamp, Constant.SOURCE_LOCAL, true))
                                                Toast.makeText(requireContext(), getString(R.string.crowdsource_contributed), Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.error), e), Toast.LENGTH_SHORT).show()
                                            }
                                }
                                .setNegativeButton(getString(R.string.button_no)) { dialog, _ -> dialog.dismiss() }.show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.error), exception), Toast.LENGTH_SHORT).show()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_SELECT_CONTACT && resultCode == AppCompatActivity.RESULT_OK) blacklistBinder.inputBlacklistNumber.setText(Core.contactPhone(requireContext(), data))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Constant.PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), Constant.PERMISSION_REQUEST_READ_CONTACTS)
        }
    }

    private fun formattedContact() {
        if (Core.checkContactsPermission(requireContext())) startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), Constant.REQUEST_SELECT_CONTACT) else ActivityCompat.requestPermissions(requireActivity(), arrayOf<String?>(Constant.READ_CONTACTS), Constant.PERMISSION_REQUEST_READ_CONTACTS)
    }
}