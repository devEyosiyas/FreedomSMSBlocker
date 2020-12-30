package dev.eyosiyas.smsblocker.service

import android.content.*
import android.os.Bundle
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat
import dev.eyosiyas.smsblocker.database.DatabaseManager
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.model.Keyword
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.InformManager
import dev.eyosiyas.smsblocker.util.PrefManager
import java.util.regex.Matcher
import java.util.regex.Pattern

class BlockerService : BroadcastReceiver() {
    private lateinit var databaseManager: DatabaseManager
    override fun onReceive(context: Context, intent: Intent) {
        val body: StringBuilder = StringBuilder()
        var number: String? = ""
        var timestamp: Long = System.currentTimeMillis()
        val bundle: Bundle? = intent.extras
        val messages: Array<SmsMessage?>
        if (bundle != null) {
            databaseManager = DatabaseManager(context)
            val msgObjects: Array<Any>? = bundle.get(Constant.SMS_BUNDLE) as Array<Any>?
            messages = arrayOfNulls(msgObjects!!.size)
            for (i in messages.indices) {
                messages[i] = SmsMessage.createFromPdu(msgObjects.get(i) as ByteArray?)
                val append = body.append(messages[i]!!.messageBody)
                number = messages[i]!!.originatingAddress
                timestamp = messages[i]!!.timestampMillis
            }
            createMessage(context, number, body.toString(), timestamp)
        }
    }

    private fun createMessage(context: Context, address: String?, body: String, timestamp: Long) {
        val manager: PrefManager = PrefManager(context)
        var isClean: Boolean = true
        for (blacklist: Blacklist? in databaseManager.blacklist) {
            if (address.equals(blacklist!!.number, ignoreCase = true)) {
                isClean = false
                break
            } else isClean = true
        }
        if (isClean && manager.isNuclearEnabled) {
            val nuclearPattern: Pattern = Pattern.compile("(?<!\\d)\\d{3,4}(?!\\d)")
            val matcher: Matcher = nuclearPattern.matcher(address)
            isClean = !matcher.find()
        }
        if (isClean && manager.isStartsWithEnabled) {
            val startsWithPattern: Pattern = Pattern.compile(String.format("^%s", manager.startsWith))
            val matcher: Matcher = startsWithPattern.matcher(address)
            isClean = !matcher.find()
        }
        if (isClean && manager.isEndsWithEnabled) {
            val endsWithPattern: Pattern = Pattern.compile(String.format("%s$", manager.endsWith))
            val matcher: Matcher = endsWithPattern.matcher(address)
            isClean = !matcher.find()
        }
        if (isClean && databaseManager.keywordsCount > 0) {
            for (keyword: Keyword in databaseManager.keywords) {
                if (body.contains(keyword.keyword + "")) {
                    isClean = false
                    break
                } else {
                    isClean = true
                }
            }
        }
        if (isClean) {
            val contentValues: ContentValues = ContentValues()
            contentValues.put(Constant.FIELD_NAME, address)
            contentValues.put(Constant.FIELD_BODY, body)
            contentValues.put(Constant.FIELD_DATE, timestamp)
            val contentResolver: ContentResolver = context.contentResolver
            contentResolver.insert(Constant.CONTENT_PROVIDER_INBOX, contentValues)
            notifyUser(context, address, body)
        }
    }

    private fun notifyUser(context: Context, number: String?, message: String) {
        val inform: InformManager = InformManager(context)
        val builder: NotificationCompat.Builder? = inform.showNotification(Core.displayName(context, number), message)
        inform.manager.notify((1 + Math.random() * 9999).toInt(), builder!!.build())
    }
}