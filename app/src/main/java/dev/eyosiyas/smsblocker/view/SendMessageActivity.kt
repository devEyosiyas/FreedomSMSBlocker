package dev.eyosiyas.smsblocker.view

import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.ActivitySendMessageBinding
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import java.util.*

class SendMessageActivity : AppCompatActivity() {
    private lateinit var binder: ActivitySendMessageBinding
    private val sentBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(context, getString(R.string.message_sent_successfully), Toast.LENGTH_SHORT).show()
                    resetUI()
                }
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, getString(R.string.generic_failure), Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, getString(R.string.no_service), Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, getString(R.string.null_pdu), Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, getString(R.string.radio_off), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val deliveredBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                RESULT_OK -> Toast.makeText(context, getString(R.string.message_delivered), Toast.LENGTH_SHORT).show()
                RESULT_CANCELED -> Toast.makeText(context, getString(R.string.message_not_received), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(sentBroadcastReceiver, IntentFilter(Constant.FILTER_SMS_SENT))
        registerReceiver(deliveredBroadcastReceiver, IntentFilter(Constant.FILTER_SMS_DELIVERED))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(sentBroadcastReceiver)
        unregisterReceiver(deliveredBroadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = Locale(PrefManager(this).locale)
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else
            configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            createConfigurationContext(configuration)
        else
            resources.updateConfiguration(configuration, resources.displayMetrics)
        binder = ActivitySendMessageBinding.inflate(layoutInflater)
        setContentView(binder.root)
        binder.receiverEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validation()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binder.messageBoxEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validation()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binder.selectContactBtn.setOnClickListener { if (Core.checkContactsPermission(this@SendMessageActivity)) startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), Constant.REQUEST_SELECT_CONTACT) else ActivityCompat.requestPermissions(this@SendMessageActivity, arrayOf<String?>(Constant.READ_CONTACTS), Constant.PERMISSION_REQUEST_READ_CONTACTS) }
        binder.sendMessageBtn.setOnClickListener { sendMessage() }
    }

    private fun validation() {
        binder.sendMessageBtn.isEnabled = binder.receiverEditText.text.length > 2 && binder.messageBoxEditText.text.isNotEmpty()
    }

    private fun sendMessage() {
        val sentPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent(Constant.FILTER_SMS_SENT), 0)
        val deliveredPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent(Constant.FILTER_SMS_DELIVERED), 0)
        if (ContextCompat.checkSelfPermission(this@SendMessageActivity, Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(binder.receiverEditText.text.toString(), null, binder.messageBoxEditText.text.toString(), sentPendingIntent, deliveredPendingIntent)
        } else Toast.makeText(this@SendMessageActivity, R.string.permission_missing, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) binder.receiverEditText.setText(Core.contactPhone(this@SendMessageActivity, data))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Constant.PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), Constant.PERMISSION_REQUEST_READ_CONTACTS)
        }
    }

    private fun resetUI() {
        binder.receiverEditText.setText("")
        binder.messageBoxEditText.setText("")
        binder.sendMessageBtn.isEnabled = false
    }
}