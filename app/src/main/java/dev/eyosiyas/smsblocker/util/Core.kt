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
import java.text.SimpleDateFormat
import java.util.*

object Core {
    fun readableTime(timestamp: Long, context: Context): String {
        val locale = Locale(PrefManager(context).locale)
        val currentDate: String = SimpleDateFormat(Constant.DATE_PATTERN, locale).format(Calendar.getInstance().time)
        val date: String = SimpleDateFormat(Constant.DATE_PATTERN, locale).format(Date(timestamp))
        return if ((date == currentDate)) SimpleDateFormat(Constant.TIME_PATTERN, locale).format(Date(timestamp)) else SimpleDateFormat(Constant.DATE_TIME_PATTERN, locale).format(Date(timestamp))
    }

    fun permissionDenied(activity: Activity, title: String?, message: String?, forced: Boolean) {
        val permissionDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        permissionDialog.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.button_yes) { _, _ -> settings(activity) }
                .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                    if (forced) activity.finish()
                    dialog.dismiss()
                }
        permissionDialog.show()
    }

    private fun settings(context: Activity) {
        Toast.makeText(context, context.getString(R.string.permission_from_settings), Toast.LENGTH_SHORT).show()
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
                defaultDialog.setTitle(R.string.default_sms_app_title)
                        .setCancelable(false)
                        .setMessage(R.string.default_sms_app_message)
                        .setPositiveButton(R.string.button_yes) { _, _ -> activity.startActivityForResult(Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.packageName), 5) }
                        .setNegativeButton(R.string.button_cancel) { dialog, _ -> dialog.dismiss() }
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
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_extra))
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_title)))
    }

    fun about(context: Context) {
        val textView = TextView(context)
        textView.autoLinkMask = 15
        textView.setText(R.string.about_content)
        textView.setPadding(50, 0, 0, 0)
        textView.textSize = 15f
        textView.setLinkTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        AlertDialog.Builder(context).setCancelable(false).setView(textView).setTitle(context.getString(R.string.menu_about)).setIcon(R.mipmap.ic_launcher).setNegativeButton(context.getString(R.string.button_back)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }.show()
    }

    private fun formattedNumber(number: String): String {
        val altered = number.replace(" ", "").replace("-", "")
        return if (altered.startsWith("+")) altered else String.format(Locale.US, "+251%s", altered.replaceFirst("0", ""))
    }
}