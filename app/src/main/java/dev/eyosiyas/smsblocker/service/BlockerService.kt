package dev.eyosiyas.smsblocker.service

import android.content.*
import android.os.Bundle
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Blocked
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.InformManager
import dev.eyosiyas.smsblocker.util.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


class BlockerService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val body: StringBuilder = StringBuilder()
        var number: String? = ""
        var timestamp: Long = System.currentTimeMillis()
        val bundle: Bundle? = intent.extras
        val messages: Array<SmsMessage?>
        if (bundle != null) {
            val msgObjects: Array<*>? = bundle.get(Constant.SMS_BUNDLE) as Array<*>?
            messages = arrayOfNulls(msgObjects!!.size)
            for (i in messages.indices) {
                messages[i] = SmsMessage.createFromPdu(msgObjects[i] as ByteArray?)
                body.append(messages[i]!!.messageBody)
                number = messages[i]!!.originatingAddress
                timestamp = messages[i]!!.timestampMillis
            }
            createMessage(context, number, body.toString(), timestamp)
        }
    }

    private fun createMessage(context: Context, address: String?, body: String, timestamp: Long) {
        val db = Storage.database(context)
        val blacklistDAO = db.blacklistDao()
        val blockedDAO = db.blockedDao()
        val keywordDAO = db.keywordDao()
        val manager = PrefManager(context)
        var isClean = true

        // TODO: 1/13/2021 check which blocking method is active and apply it accordingly
//        for (blacklist: Blacklist? in blacklistDAO.blacklists()) {
////        for (blacklist: Blacklist? in databaseManager.blacklist) {
//            if (address.equals(blacklist!!.number, ignoreCase = true)) {
//                isClean = false
//                break
//            } else isClean = true
//        }
        if (isClean && manager.nuclearOption) {
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
//        if (isClean && keywordDAO.keywordsCount() > 0) {
////        if (isClean && databaseManager.keywordsCount > 0) {
//            for (keyword: Keyword in keywordDAO.keywords()) {
////            for (keyword: Keyword in databaseManager.keywords) {
//                if (body.contains(keyword.keyword)) {
//                    isClean = false
//                    break
//                } else {
//                    isClean = true
//                }
//            }
//        }
        if (isClean) {
            val contentValues = ContentValues()
            contentValues.put(Constant.FIELD_NAME, address)
            contentValues.put(Constant.FIELD_BODY, body)
            contentValues.put(Constant.FIELD_DATE, timestamp)
            val contentResolver: ContentResolver = context.contentResolver
            contentResolver.insert(Constant.CONTENT_PROVIDER_INBOX, contentValues)
            notifyUser(context, address, body)
        } else
            GlobalScope.launch(Dispatchers.Main) {
                blockedDAO.addBlocked(Blocked(0, address ?: "Uknown Sender", body, timestamp))
            }
    }

    private fun notifyUser(context: Context, number: String?, message: String) {
        val inform = InformManager(context)
        val builder: NotificationCompat.Builder = inform.showNotification(Core.displayName(context, number), message)
        inform.manager.notify((1..9999).random(), builder.build())
    }
}