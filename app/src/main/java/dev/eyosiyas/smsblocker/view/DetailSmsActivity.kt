package dev.eyosiyas.smsblocker.view

import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dev.eyosiyas.smsblocker.adapter.MessageListAdapter
import dev.eyosiyas.smsblocker.databinding.ActivityDetailSmsBinding
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import java.util.*

class DetailSmsActivity : AppCompatActivity() {
    private lateinit var binder: ActivityDetailSmsBinding
    private var messageListAdapter: MessageListAdapter? = null
    private var number: String? = null
    private var key: String? = null
    private var isNumber: Boolean = false
    private val sentBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(context, "Message sent successfully.", Toast.LENGTH_SHORT).show()
                    reload()
                }
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, "RESULT_ERROR_GENERIC_FAILURE", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, "ERROR_NO_SERVICE", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, "ERROR_NULL_PDU", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, "ERROR_RADIO_OFF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reload() {
        messageListAdapter = MessageListAdapter(messages(key), this)
        binder.detailMessageRecyclerView.adapter = messageListAdapter
        binder.detailMessageRecyclerView.post { binder.detailMessageRecyclerView.scrollToPosition(messageListAdapter!!.itemCount - 1) }
    }

    private val deliveredBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                RESULT_OK -> Toast.makeText(context, "Message delivered to recipient.", Toast.LENGTH_SHORT).show()
                RESULT_CANCELED -> Toast.makeText(context, "Message not received by recipient.", Toast.LENGTH_SHORT).show()
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
        binder = ActivityDetailSmsBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setSupportActionBar(binder.detailToolbar)
        binder.detailToolbar.setNavigationOnClickListener { onBackPressed() }
        key = intent.getStringExtra("KEY")
        val display: String? = intent.getStringExtra("DISPLAY")
        if (key == null) onBackPressed()
        binder.detailToolbar.title = display ?: key
        binder.btnSendMessage.setOnClickListener { sendMessage() }
        binder.detailMessageRecyclerView.layoutManager = LinearLayoutManager(this)
        reload()
        binder.editMessageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validation()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun messages(search: String?): List<Message?> {
        val messages: MutableList<Message?> = ArrayList()
        var message: Message
        val cursor: Cursor? = contentResolver.query(Constant.CONTENT_PROVIDER_SMS, null, Constant.FIELD_NAME + "=?", arrayOf(search), null)
        while (cursor!!.moveToNext()) {
            number = cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))
            message = Message(
                    sender = number,
                    displayName = Core.displayName(this, cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))),
                    body = cursor.getString(cursor.getColumnIndex(Constant.FIELD_BODY)),
                    timestamp = cursor.getLong(cursor.getColumnIndex(Constant.FIELD_DATE)),
                    type = if (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_TYPE)) == 1) Constant.FIELD_RECEIVED else Constant.FIELD_SENT,
                    isRead = cursor.getInt(cursor.getColumnIndex(Constant.FIELD_READ)) != 0,
                    isSeen = cursor.getInt(cursor.getColumnIndex(Constant.FIELD_SEEN)) != 0,
                    image = null
            )
            isNumber = number!!.matches("\\+*[0-9]*".toRegex()) && number!!.length > 2
//            message.sender =
//            message.displayName = (Core.displayName(this, cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))))
//            message.body = (cursor.getString(cursor.getColumnIndex(Constant.FIELD_BODY)))
//            message.isRead = (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_READ)) != 0)
//            message.isSeen = (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_SEEN)) != 0)
//            message.timestamp = (cursor.getLong(cursor.getColumnIndex(Constant.FIELD_DATE)))
//            message.type = (if (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_TYPE)) == 1) Constant.FIELD_RECEIVED else Constant.FIELD_SENT)
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
        val sentPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent(Constant.FILTER_SMS_SENT), 0)
        val deliveredPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent(Constant.FILTER_SMS_DELIVERED), 0)
        if (ContextCompat.checkSelfPermission(this@DetailSmsActivity, Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, binder.editMessageBox.text.toString(), sentPendingIntent, deliveredPendingIntent)
            binder.editMessageBox.setText("")
            binder.btnSendMessage.isEnabled = false
        } else Toast.makeText(this@DetailSmsActivity, "Permission missing.", Toast.LENGTH_SHORT).show()
    }
}
