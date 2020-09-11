package dev.eyosiyas.smsblocker.util;

import android.Manifest;
import android.net.Uri;
import android.provider.ContactsContract;

public class Constant {
    public static final String DATE_PATTERN = "MMM dd yyyy";
    public static final String TIME_PATTERN = "hh:mm:ss a";
    public static final String DATE_TIME_PATTERN = "MMM dd yyyy hh:mm:ss a";
    public static final int PERMISSION_REQUEST_READ_SMS = 2010;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 2011;
    public static final int REQUEST_SETTING = 2013;
    public static final int REQUEST_SELECT_CONTACT = 2014;
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String READ_SMS = Manifest.permission.READ_SMS;
    public static final String SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public static final String FILTER_SMS_DELIVERED = "SMS_DELIVERED";
    public static final String FILTER_SMS_SENT = "SMS_SENT";
    public static final String FIELD_NAME = "address";
    public static final String FIELD_BODY = "body";
    public static final String FIELD_READ = "read";
    public static final String FIELD_SEEN = "seen";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_RECEIVED = "received";
    public static final String FIELD_SENT = "sent";
    public static final Uri CONTENT_PROVIDER_SMS = Uri.parse("content://sms/");
    public static final Uri CONTENT_PROVIDER_INBOX = Uri.parse("content://sms/inbox");
    public static final String SMS_BUNDLE = "pdus";
    public static final String NUCLEAR = "nuclear";
    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";
}
