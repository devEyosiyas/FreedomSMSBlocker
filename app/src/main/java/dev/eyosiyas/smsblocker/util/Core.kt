package dev.eyosiyas.smsblocker.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Telephony
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.R.string
import dev.eyosiyas.smsblocker.util.Constant.DATE_MONTH_PATTERN
import dev.eyosiyas.smsblocker.util.Constant.DATE_MONTH_YEAR_PATTERN
import dev.eyosiyas.smsblocker.util.Constant.DAY_PATTERN
import dev.eyosiyas.smsblocker.util.Constant.DEFAULT_PROFILE
import dev.eyosiyas.smsblocker.util.Constant.EPOCH_MULTIPLIER
import java.text.SimpleDateFormat
import java.util.*


object Core {
    fun readableTime(timestamp: Long, context: Context): String {
        val locale = Locale(PrefManager(context).locale)
        val currentDate: String = SimpleDateFormat(Constant.DATE_PATTERN, locale).format(Calendar.getInstance().time)
        val date: String = SimpleDateFormat(Constant.DATE_PATTERN, locale).format(Date(timestamp))
        return if ((date == currentDate)) SimpleDateFormat(Constant.TIME_PATTERN, locale).format(Date(timestamp)) else SimpleDateFormat(Constant.DATE_TIME_PATTERN, locale).format(Date(timestamp))
    }

    fun interactiveTime(timestamp: Long, context: Context): String {
        val locale = Locale(PrefManager(context).locale)
        val seconds = Calendar.getInstance().time.time - Date(timestamp).time / EPOCH_MULTIPLIER
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return when {
            seconds == 0L -> context.getString(string.time_now)
            seconds in 1..59 -> context.getString(string.time_sec, seconds)
            minutes in 1..59 -> context.getString(string.time_min, minutes)
            hours in 1..23 -> context.getString(string.time_hour, hours)
            days == 1L -> context.getString(string.time_yesterday)
            days in 2..7 -> SimpleDateFormat(DAY_PATTERN, locale).format(Date(timestamp))
            days in 8..365 -> SimpleDateFormat(DATE_MONTH_PATTERN, locale).format(Date(timestamp))
            else -> SimpleDateFormat(DATE_MONTH_YEAR_PATTERN, locale).format(Date(timestamp))
        }
    }

    fun permissionDenied(activity: Activity, title: String?, message: String?, forced: Boolean) {
        val permissionDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        permissionDialog.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(string.button_yes) { _, _ -> settings(activity) }
                .setNegativeButton(string.button_cancel) { dialog, _ ->
                    if (forced) activity.finish()
                    dialog.dismiss()
                }
        permissionDialog.show()
    }

    private fun settings(context: Activity) {
        Toast.makeText(context, context.getString(string.permission_from_settings), Toast.LENGTH_SHORT).show()
        context.startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", context.applicationInfo.packageName, null)), Constant.REQUEST_SETTING)
    }

    fun displayName(context: Context, number: String): String {
        var tmpNumber: String = number
        if (ContextCompat.checkSelfPermission((context), Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) return tmpNumber
        val uri: Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(tmpNumber))
        val cursor: Cursor? = context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        cursor?.use { _ ->
            if (cursor.moveToFirst()) tmpNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
        }
        return tmpNumber
    }

    fun contactPicture(context: Context, number: String): String {
        var location: String = DEFAULT_PROFILE
        if (ContextCompat.checkSelfPermission((context), Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) return location
        val uri: Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val cursor: Cursor? = context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.PHOTO_URI), null, null, null)
        cursor?.use { _ ->
            if (cursor.moveToFirst())
                location = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI))
                        ?: DEFAULT_PROFILE
        }
        return location
    }

    fun checkContactsPermission(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission((context)!!, Constant.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

    fun contactPhone(context: Context, data: Intent?): String {
        var number: String? = null
        val uri: Uri? = data!!.data
        val cursor: Cursor? = context.contentResolver.query((uri)!!, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                number = formattedNumber(cursor.getString(cursor.getColumnIndex(Constant.NUMBER)))
            }
            cursor.close()
        }
        return number ?: ""
    }

    fun defaultSMS(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (activity.packageName != Telephony.Sms.getDefaultSmsPackage(activity)) {
                val defaultDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
                defaultDialog.setTitle(string.default_sms_app_title)
                        .setCancelable(false)
                        .setMessage(string.default_sms_app_message)
                        .setPositiveButton(string.button_yes) { _, _ -> activity.startActivityForResult(Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.packageName), 5) }
                        .setNegativeButton(string.button_cancel) { dialog, _ -> dialog.dismiss() }
                defaultDialog.show()
            }
        }
    }

    fun limit(input: CharSequence): String {
        return if (input.toString().matches("[a-zA-Z]".toRegex())) input.toString() else ""
    }

    fun telegram(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("tg:resolve?domain=spamFreeLife")))
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/spamFreeLife")))
        }
    }

    fun share(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(string.share_extra))
        context.startActivity(Intent.createChooser(intent, context.getString(string.share_title)))
    }

    fun about(context: Context) {
        val textView = TextView(context)
        textView.autoLinkMask = 15
        textView.setText(string.about_content)
        textView.setPadding(50, 0, 0, 0)
        textView.textSize = 15f
        textView.setLinkTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        AlertDialog.Builder(context).setCancelable(false).setView(textView).setTitle(context.getString(string.menu_about)).setIcon(R.mipmap.ic_launcher).setNegativeButton(context.getString(string.button_back)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }.show()
    }

    private fun formattedNumber(number: String): String {
        val altered = number.replace(" ", "").replace("-", "")
        return if (altered.startsWith("+")) altered else String.format(Locale.US, "+251%s", altered.replaceFirst("0", ""))
    }
}