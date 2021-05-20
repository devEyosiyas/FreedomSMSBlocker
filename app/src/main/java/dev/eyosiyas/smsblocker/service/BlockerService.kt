package dev.eyosiyas.smsblocker.service

import android.content.*
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat
import dev.eyosiyas.smsblocker.database.Storage
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.model.Blocked
import dev.eyosiyas.smsblocker.model.Keyword
import dev.eyosiyas.smsblocker.model.Whitelist
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Constant.SMS_FORMAT
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
        var number = ""
        var timestamp: Long = System.currentTimeMillis()
        val bundle: Bundle? = intent.extras
        val messages: Array<SmsMessage?>

        val format = bundle!!.getString(SMS_FORMAT)
        val msgObjects: Array<*>? = bundle.get(Constant.SMS_BUNDLE) as Array<*>?
        messages = arrayOfNulls(msgObjects!!.size)
        for (i in messages.indices) {
            messages[i] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                SmsMessage.createFromPdu(msgObjects[i] as ByteArray, format)
            else
                SmsMessage.createFromPdu(msgObjects[i] as ByteArray)
            body.append(messages[i]!!.messageBody)
            number = messages[i]!!.originatingAddress.toString()
            timestamp = messages[i]!!.timestampMillis
        }
//            for (i in messages.indices) {
//                messages[i] = SmsMessage.createFromPdu(msgObjects[i] as ByteArray?)
//                body.append(messages[i]!!.messageBody)
//                number = messages[i]!!.originatingAddress.toString()
//                timestamp = messages[i]!!.timestampMillis
//            }
        GlobalScope.launch(Dispatchers.IO) {
            createMessage(context, number, body.toString(), timestamp)
        }
    }

    private suspend fun createMessage(context: Context, address: String, body: String, timestamp: Long) {
        val db = Storage.database(context)
        val blacklistDAO = db.blacklistDao()
        val blockedDAO = db.blockedDao()
        val keywordDAO = db.keywordDao()
        val whitelistDAO = db.whitelistDao()
        val manager = PrefManager(context)
        var isClean = true

        if (blacklistDAO.blacklistCount() > 0) {
            for (blacklist: Blacklist in blacklistDAO.pureBlacklists()) {
                if (address.equals(blacklist.number, ignoreCase = true)) {
                    isClean = false
                    break
                }
            }
        }

        if (isClean && keywordDAO.keywordsCount() > 0) {
            for (keyword: Keyword in keywordDAO.pureKeywords()) {
                if (body.contains(keyword.keyword)) {
                    isClean = false
                    break
                }
            }
        }

        if (isClean) {
            var matcher: Matcher
            when (manager.blockingRule) {
                Constant.STARTS_WITH_TAG -> {
                    val startsWithPattern: Pattern = Pattern.compile(String.format("^%s", manager.startsWith))
                    if (whitelistDAO.whitelistCount() > 0) {
                        for (whitelist: Whitelist in whitelistDAO.pureWhitelists()) {
                            if (!address.equals(whitelist.number, ignoreCase = true)) {
                                matcher = startsWithPattern.matcher(address)
                                if (matcher.find()) {
                                    isClean = false
                                    break
                                }
                            }
                        }
                    } else
                        isClean = !startsWithPattern.matcher(address).find()
                }
                Constant.ENDS_WITH_TAG -> {
                    val endsWithPattern: Pattern = Pattern.compile(String.format("%s$", manager.endsWith))
                    if (whitelistDAO.whitelistCount() > 0) {
                        for (whitelist: Whitelist in whitelistDAO.pureWhitelists()) {
                            if (!address.equals(whitelist.number, ignoreCase = true)) {
                                matcher = endsWithPattern.matcher(address)
                                if (matcher.find()) {
                                    isClean = false
                                    break
                                }
                            }
                        }
                    } else
                        isClean = !endsWithPattern.matcher(address).find()
                }
                Constant.NUCLEAR_OPTION_TAG -> {
                    val nuclearPattern: Pattern = Pattern.compile("(?<!\\d)\\d{3,4}(?!\\d)")
                    if (whitelistDAO.whitelistCount() > 0) {
                        for (whitelist: Whitelist in whitelistDAO.pureWhitelists()) {
                            if (!address.equals(whitelist.number, ignoreCase = true)) {
                                matcher = nuclearPattern.matcher(address)
                                if (matcher.find()) {
                                    isClean = false
                                    break
                                }
                            }
                        }
                    } else
                        isClean = !nuclearPattern.matcher(address).find()
                }
            }
        }

        if (isClean) {
            val contentValues = ContentValues()
            contentValues.put(Constant.FIELD_NAME, address)
            contentValues.put(Constant.FIELD_BODY, body)
            contentValues.put(Constant.FIELD_DATE, timestamp)
            val contentResolver: ContentResolver = context.contentResolver
            contentResolver.insert(Constant.CONTENT_PROVIDER_INBOX, contentValues)
            notifyUser(context, address, body)
        } else
            blockedDAO.addBlocked(Blocked(0, address, body, timestamp))
    }

    private fun notifyUser(context: Context, number: String, message: String) {
        val inform = InformManager(context)
        val builder: NotificationCompat.Builder = inform.showNotification(Core.displayName(context, number), message)
        inform.manager.notify((1..9999).random(), builder.build())
    }
}