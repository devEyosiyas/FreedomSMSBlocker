package dev.eyosiyas.smsblocker.util

import android.Manifest
import android.net.Uri
import android.provider.ContactsContract
import android.text.InputFilter

object Constant {
    const val ID: String = "ID"
    const val FIRST_RUN: String = "FirstRun"
    const val LANGUAGE: String = "Language"
    const val BLOCKING_RULE: String = "Blocking Rule"
    const val DATE_PATTERN: String = "MMM dd yyyy"
    const val TIME_PATTERN: String = "hh:mm:ss a"
    const val DATE_TIME_PATTERN: String = "MMM dd yyyy hh:mm:ss a"
    const val STARTS_WITH_TAG: String = "SW"
    const val ENDS_WITH_TAG: String = "EW"
    const val NUCLEAR_OPTION_TAG: String = "NO"
    const val PERMISSION_REQUEST_READ_SMS: Int = 2010
    const val PERMISSION_REQUEST_READ_CONTACTS: Int = 2011
    const val REQUEST_SETTING: Int = 2013
    const val REQUEST_SELECT_CONTACT: Int = 2014
    const val READ_CONTACTS: String = Manifest.permission.READ_CONTACTS
    const val READ_SMS: String = Manifest.permission.READ_SMS
    const val SEND_SMS: String = Manifest.permission.SEND_SMS
    const val NUMBER: String = ContactsContract.CommonDataKinds.Phone.NUMBER
    const val FILTER_SMS_DELIVERED: String = "SMS_DELIVERED"
    const val FILTER_SMS_SENT: String = "SMS_SENT"
    const val FIELD_NAME: String = "address"
    const val FIELD_BODY: String = "body"
    const val FIELD_READ: String = "read"
    const val FIELD_SEEN: String = "seen"
    const val FIELD_DATE: String = "date"
    const val FIELD_TYPE: String = "type"
    const val FIELD_RECEIVED: String = "received"
    const val FIELD_SENT: String = "sent"
    val CONTENT_PROVIDER_SMS: Uri = Uri.parse("content://sms/")
    val CONTENT_PROVIDER_INBOX: Uri = Uri.parse("content://sms/inbox")
    const val SMS_BUNDLE: String = "pdus"
    const val NUCLEAR: String = "nuclear"
    const val STARTS_WITH: String = "startsWith"
    const val ENDS_WITH: String = "endsWith"
    val INPUT_LIMIT = InputFilter.LengthFilter(15)
}