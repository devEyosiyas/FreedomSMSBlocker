package dev.eyosiyas.smsblocker.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.MessageAdapter
import dev.eyosiyas.smsblocker.databinding.FragmentMessageBinding
import dev.eyosiyas.smsblocker.event.MessageSelected
import dev.eyosiyas.smsblocker.model.Message
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import dev.eyosiyas.smsblocker.view.DetailSmsActivity
import dev.eyosiyas.smsblocker.view.SendMessageActivity
import java.util.*

class MessageFragment : Fragment(), MessageSelected {
    private lateinit var binder: FragmentMessageBinding

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
        binder = FragmentMessageBinding.bind(inflater.inflate(R.layout.fragment_message, container, false))
        binder.fabSendMessage.setOnClickListener {
            if (ContextCompat.checkSelfPermission((activity)!!, Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED) startActivity(Intent(context, SendMessageActivity::class.java)) else Toast.makeText(context, "You need permission", Toast.LENGTH_SHORT).show()
        }
        binder.smsListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binder.smsListRecycler.adapter = MessageAdapter(this, messages())
        return binder.root
    }

    private fun messages(): List<Message> {
        val messages: MutableList<Message> = ArrayList()
        val list: MutableList<String> = ArrayList()
        val cursor: Cursor? = requireActivity().contentResolver.query(Constant.CONTENT_PROVIDER_SMS, null, null, null, null)
        while (cursor!!.moveToNext()) {
            if (!list.contains(cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)))) {
                list.add(cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)))
                messages.add(Message(0, cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME)), Core.displayName(requireContext(), cursor.getString(cursor.getColumnIndex(Constant.FIELD_NAME))), if (cursor.getInt(cursor.getColumnIndex(Constant.FIELD_TYPE)) == 1) Constant.FIELD_RECEIVED else Constant.FIELD_SENT, cursor.getString(cursor.getColumnIndex(Constant.FIELD_BODY)), cursor.getInt(cursor.getColumnIndex(Constant.FIELD_READ)) != 0, cursor.getInt(cursor.getColumnIndex(Constant.FIELD_SEEN)) != 0, cursor.getLong(cursor.getColumnIndex(Constant.FIELD_DATE))))
            }
        }
        cursor.close()
        list.clear()
        return messages
    }

    override fun onMessageSelected(message: Message, displayName: String) {
        startActivity(Intent(context, DetailSmsActivity::class.java).putExtra("KEY", message.sender).putExtra("DISPLAY", displayName))
    }
}