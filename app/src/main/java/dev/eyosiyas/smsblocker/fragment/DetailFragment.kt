package dev.eyosiyas.smsblocker.fragment

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.MessageListAdapter
import dev.eyosiyas.smsblocker.databinding.FragmentDetailBinding
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.view.MainActivity
import java.util.*


class DetailFragment : Fragment() {
    private lateinit var binder: FragmentDetailBinding

    //    private var messageListAdapter: MessageListAdapter? = null
    private val messageListAdapter by lazy { MessageListAdapter() }
    private var number: String? = null
    private lateinit var key: String
    private var isNumber: Boolean = false
    private val args: DetailFragmentArgs by navArgs()

    private val sentBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    Toast.makeText(context, getString(R.string.message_sent_successfully), Toast.LENGTH_SHORT).show()
                    reload()
                }
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, getString(R.string.generic_failure), Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, getString(R.string.no_service), Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, getString(R.string.null_pdu), Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, getString(R.string.radio_off), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reload() {
//        GlobalScope.launch(Dispatchers.Default)
        messageListAdapter.populate(messages(key))
        binder.detailMessageRecyclerView.adapter = messageListAdapter
        binder.detailMessageRecyclerView.post { binder.detailMessageRecyclerView.scrollToPosition(messageListAdapter.itemCount - 1) }
    }

    private val deliveredBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> Toast.makeText(context, getString(R.string.message_delivered), Toast.LENGTH_SHORT).show()
                AppCompatActivity.RESULT_CANCELED -> Toast.makeText(context, getString(R.string.message_not_received), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(sentBroadcastReceiver, IntentFilter(Constant.FILTER_SMS_SENT))
        requireContext().registerReceiver(deliveredBroadcastReceiver, IntentFilter(Constant.FILTER_SMS_DELIVERED))
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(sentBroadcastReceiver)
        requireContext().unregisterReceiver(deliveredBroadcastReceiver)
    }

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
        binder = FragmentDetailBinding.bind(inflater.inflate(R.layout.fragment_detail, container, false))
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        key = args.Key
        if (key == "Empty") Navigation.findNavController(view).navigate(DetailFragmentDirections.navigateToViewPager())
        (activity as MainActivity).supportActionBar?.title = args.Display
        binder.btnSendMessage.setOnClickListener { sendMessage() }
        binder.detailMessageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reload()
        binder.editMessageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validation()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.call -> Toast.makeText(requireContext(), "Call selected", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun messages(search: String): List<Message> {
        val messages: MutableList<Message> = ArrayList()
        val cursor: Cursor? = requireContext().contentResolver.query(Constant.CONTENT_PROVIDER_SMS, null, Constant.FIELD_NAME + "=?", arrayOf(search), null)
        while (cursor!!.moveToNext()) {
            number = cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))
            isNumber = number!!.matches("\\+*[0-9]*".toRegex()) && number!!.length > 2
            val message = Message(0, cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)), Core.displayName(requireContext(), cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))), if (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_TYPE)) == 1) Constant.FIELD_RECEIVED else Constant.FIELD_SENT, cursor.getString(cursor.getColumnIndex(Constant.FIELD_BODY)), cursor.getInt(cursor.getColumnIndex(Constant.FIELD_READ)) != 0, cursor.getInt(cursor.getColumnIndex(Constant.FIELD_SEEN)) != 0, cursor.getLong(cursor.getColumnIndex(Constant.FIELD_DATE)))
            message.picture = Core.contactPicture(requireContext(), cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)))
            messages.add(message)
        }
        cursor.close()
        messages.reverse()
        return messages
    }

    private fun validation() {
        binder.btnSendMessage.isEnabled = isNumber && binder.editMessageBox.text.isNotEmpty()
    }

    private fun sendMessage() {
        val sentPendingIntent: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent(Constant.FILTER_SMS_SENT), 0)
        val deliveredPendingIntent: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent(Constant.FILTER_SMS_DELIVERED), 0)
        if (ContextCompat.checkSelfPermission(requireContext(), Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, binder.editMessageBox.text.toString(), sentPendingIntent, deliveredPendingIntent)
            binder.editMessageBox.text.clear()
            binder.btnSendMessage.isEnabled = false
        } else Toast.makeText(requireContext(), getString(R.string.permission_missing), Toast.LENGTH_SHORT).show()
    }
}