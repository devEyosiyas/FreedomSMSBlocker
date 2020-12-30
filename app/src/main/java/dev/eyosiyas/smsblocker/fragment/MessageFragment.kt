package dev.eyosiyas.smsblocker.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.MessageAdapter
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.view.SendMessageActivity
import java.util.*

class MessageFragment : Fragment() {
    private lateinit var smsRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_message, container, false)
        val sendSMSActionButton: FloatingActionButton = view.findViewById(R.id.fabSendMessage)
        smsRecyclerView = view.findViewById(R.id.smsListRecycler)
        sendSMSActionButton.setOnClickListener { if (ContextCompat.checkSelfPermission((activity)!!, Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED) startActivity(Intent(context, SendMessageActivity::class.java)) else Toast.makeText(context, "You need permission", Toast.LENGTH_SHORT).show() }
        smsRecyclerView.layoutManager = LinearLayoutManager(context)
        init()
        return view
    }

    private val messages: List<Message>
        get() {
            val messages: MutableList<Message> = ArrayList()
            val list: MutableList<String> = ArrayList()
            var message: Message
            val cursor: Cursor? = requireActivity().contentResolver.query(Constant.CONTENT_PROVIDER_SMS, null, null, null, null)
            while (cursor!!.moveToNext()) {
                if (!list.contains(cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)))) {
//                    message = Message()
//                    message.setSender(cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)))
//                    message.setDisplayName(Core.getDisplayName(context, cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))))
                    list.add(cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)))
//                    message.setBody(cursor.getString(cursor.getColumnIndex(Constant.FIELD_BODY)))
//                    message.setRead(cursor.getInt(cursor.getColumnIndex(Constant.FIELD_READ)) != 0)
//                    message.setSeen(cursor.getInt(cursor.getColumnIndex(Constant.FIELD_SEEN)) != 0)
//                    message.setTimestamp(cursor.getLong(cursor.getColumnIndex(Constant.FIELD_DATE)))
//                    message.setType(if (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_TYPE)) == 1) Constant.FIELD_RECEIVED else Constant.FIELD_SENT)
//                    messages.add(message)
                    messages.add(Message(
                            if (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_TYPE)) == 1) Constant.FIELD_RECEIVED else Constant.FIELD_SENT,
                            cursor.getInt(cursor.getColumnIndex(Constant.FIELD_READ)) != 0,
                            cursor.getInt(cursor.getColumnIndex(Constant.FIELD_SEEN)) != 0,
                            cursor.getString(cursor.getColumnIndex(Constant.FIELD_BODY)),
                            cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)),
                            Core.displayName(context, cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))),
                            null,
                            cursor.getLong(cursor.getColumnIndex(Constant.FIELD_DATE))
                    ))
                }
            }
            cursor.close()
            list.clear()
            return messages
        }

    private fun init() {
        val messageAdapter: MessageAdapter = MessageAdapter(context, messages)
        smsRecyclerView.adapter = messageAdapter
    }
}