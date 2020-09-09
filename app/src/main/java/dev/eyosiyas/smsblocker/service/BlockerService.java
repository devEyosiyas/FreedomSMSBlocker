package dev.eyosiyas.smsblocker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class BlockerService extends BroadcastReceiver {
    private static final String TAG = "BlockerService";
    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                else
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }
            Log.d(TAG, "onReceive() returned: " + smsMessageStr);

        }
    }
}
