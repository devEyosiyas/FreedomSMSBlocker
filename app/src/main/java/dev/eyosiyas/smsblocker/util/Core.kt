package dev.eyosiyas.smsblocker.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Telephony
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

object Core {
    fun readableTime(timestamp: Long): String {
        val currentDate: String = SimpleDateFormat(Constant.DATE_PATTERN, Locale.ENGLISH).format(Calendar.getInstance().time)
        val date: String = SimpleDateFormat(Constant.DATE_PATTERN, Locale.ENGLISH).format(Date(timestamp))
        return if ((date == currentDate)) SimpleDateFormat(Constant.TIME_PATTERN, Locale.ENGLISH).format(Date(timestamp)) else SimpleDateFormat(Constant.DATE_TIME_PATTERN, Locale.ENGLISH).format(Date(timestamp))
    }

    fun permissionDenied(activity: Activity, title: String?, message: String?, forced: Boolean) {
        val permissionDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        permissionDialog.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Yes") { _, _ -> settings(activity) }
                .setNegativeButton("Cancel") { dialog, _ ->
                    if (forced) activity.finish()
                    dialog.dismiss()
                }
        permissionDialog.show()
    }

    private fun settings(context: Activity) {
        Toast.makeText(context, "Allow Permission form settings", Toast.LENGTH_SHORT).show()
        context.startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", context.applicationInfo.packageName, null)), Constant.REQUEST_SETTING)
    }

    fun displayName(context: Context, number: String?): String? {
        var tmpNumber: String? = number
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
                number = cursor.getString(cursor.getColumnIndex(Constant.NUMBER))
            }
            cursor.close()
        }
        return number ?: ""
    }

    fun defaultSMS(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (activity.packageName != Telephony.Sms.getDefaultSmsPackage(activity)) {
                val defaultDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
                defaultDialog.setTitle("Default SMS app")
                        .setCancelable(false)
                        .setMessage("For a better use of the app, you need to make this app a default for SMS.")
                        .setPositiveButton("Yes") { _, _ -> activity.startActivityForResult(Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.packageName), 5) }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                defaultDialog.show()
            }
        }
    }

    fun limit(input: CharSequence): String {
        return if (input.toString().matches("[a-zA-Z]".toRegex())) input.toString() else ""
    }
}