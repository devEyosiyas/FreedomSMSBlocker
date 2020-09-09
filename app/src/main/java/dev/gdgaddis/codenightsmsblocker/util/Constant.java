package dev.gdgaddis.codenightsmsblocker.util;

import android.Manifest;
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
}
