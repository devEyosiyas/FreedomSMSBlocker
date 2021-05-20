package dev.eyosiyas.smsblocker.fragment

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.CrowdSourceAdapter
import dev.eyosiyas.smsblocker.databinding.FragmentCrowdSourceBinding
import dev.eyosiyas.smsblocker.event.CrowdSourceSelected
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Constant.EPOCH_MULTIPLIER
import dev.eyosiyas.smsblocker.util.Constant.FIELD_NUMBER
import dev.eyosiyas.smsblocker.util.Constant.FIELD_TIMESTAMP
import dev.eyosiyas.smsblocker.util.Constant.SOURCE_REMOTE
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.viewmodel.BlacklistViewModel
import kotlinx.coroutines.launch
import java.util.*


class CrowdSourceFragment : Fragment(), CrowdSourceSelected {
    private lateinit var viewModel: BlacklistViewModel
    private lateinit var binder: FragmentCrowdSourceBinding
    private val blacklists = ArrayList<Blacklist>()
    private val adapter = CrowdSourceAdapter(this)

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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = FragmentCrowdSourceBinding.bind(inflater.inflate(R.layout.fragment_crowd_source, container, false))
        binder.crowdSourceRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this).get(BlacklistViewModel::class.java)
        binder.crowdSourceRecycler.adapter = adapter
//        binder.progressCircular.visibility = View.VISIBLE
//        val settings = FirebaseFirestoreSettings.Builder()
//                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
//                .setPersistenceEnabled(true)
//                .build()
//        val db = Firebase.firestore
//        db.firestoreSettings = settings
//        db.collection(Constant.PATH_SHORT_CODE)
//                .get()
//                .addOnSuccessListener { result ->
//                    runBlocking {
//                        for (document in result) {
//                            if (!viewModel.exists(document.data[FIELD_NUMBER].toString()))
//                                blacklists.add(Blacklist(0, document.data[FIELD_NUMBER].toString(), document.data[FIELD_TIMESTAMP].toString().toLong() * EPOCH_MULTIPLIER, SOURCE_REMOTE, true))
//                        }
//                    }
//                    adapter.populate(blacklists)
//                    binder.progressCircular.visibility = View.GONE
//                }
//                .addOnFailureListener { exception ->
//                    binder.progressCircular.visibility = View.GONE
//                    Toast.makeText(requireContext(), getString(R.string.error_fetching_data) + "\n$exception", Toast.LENGTH_SHORT).show()
//                }
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.progressCircular.visibility = View.VISIBLE
        val settings = FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(true)
                .build()
        val db = Firebase.firestore

        lifecycleScope.launch {

            db.firestoreSettings = settings
            db.collection(Constant.PATH_SHORT_CODE)
                    .get()
                    .addOnSuccessListener { result ->
//                    runBlocking {
                        for (document in result) {
//                            if (!viewModel.exists(document.data[FIELD_NUMBER].toString()))
                            blacklists.add(Blacklist(0, document.data[FIELD_NUMBER].toString(), document.data[FIELD_TIMESTAMP].toString().toLong() * EPOCH_MULTIPLIER, SOURCE_REMOTE, true))
                        }
//                    }
                        adapter.populate(blacklists)
                        binder.progressCircular.visibility = View.GONE
                    }
                    .addOnFailureListener { exception ->
                        binder.progressCircular.visibility = View.GONE
                        Toast.makeText(requireContext(), getString(R.string.error_fetching_data) + "\n$exception", Toast.LENGTH_SHORT).show()
                    }
        }

    }

    override fun onInsertSelected(blacklist: Blacklist) {
        insertCrowdSourced(blacklist)
    }

    override fun onLongPress(position: Int) {
        adapter.checker(position)
        Log.i(TAG, "onLongPress: position $position")
    }

    override fun onActivate(show: Boolean) {
        setMenuVisibility(show)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.crowdsource_menu, menu)
//        menu.findItem(R.id.crowdSourceSelectAll).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.crowdSourceSelectAll)
            adapter.selectAll(!adapter.allSelected())
        return super.onOptionsItemSelected(item)
    }

    private fun insertCrowdSourced(blacklist: Blacklist) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.insert_crowdsourced_title))
                .setCancelable(false)
                .setMessage(String.format(Locale.ENGLISH, getString(R.string.insert_crowdsourced_message), blacklist.number))
                .setPositiveButton(getString(R.string.button_yes)) { dialog, _ ->
                    viewModel.addBlacklist(blacklist)
                    blacklists.remove(blacklist)
                    adapter.populate(blacklists)
                    Toast.makeText(requireContext(), String.format(Locale.ENGLISH, getString(R.string.saved_to_db), blacklist.number), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.button_no)) { dialog, _ -> dialog.dismiss() }.show()
    }

    companion object {
        private const val TAG = "CrowdSourceFragment"
    }
}