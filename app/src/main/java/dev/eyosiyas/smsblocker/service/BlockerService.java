package dev.eyosiyas.smsblocker.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;

import dev.eyosiyas.smsblocker.database.DatabaseManager;
import dev.eyosiyas.smsblocker.model.Blacklist;
import dev.eyosiyas.smsblocker.util.InformManager;

import static dev.eyosiyas.smsblocker.util.Constant.CONTENT_PROVIDER_INBOX;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_BODY;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_DATE;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_NAME;
import static dev.eyosiyas.smsblocker.util.Constant.SMS_BUNDLE;

public class BlockerService extends BroadcastReceiver {
    private DatabaseManager databaseManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder body = new StringBuilder();
        String number = "";
        long timestamp = System.currentTimeMillis();
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        if (bundle != null) {
            databaseManager = new DatabaseManager(context);
            Object[] msgObjects = (Object[]) bundle.get(SMS_BUNDLE);
            messages = new SmsMessage[msgObjects.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) msgObjects[i]);
                body.append(messages[i].getMessageBody());
                number = messages[i].getOriginatingAddress();
                timestamp = messages[i].getTimestampMillis();
            }
            createMessage(context, number, body.toString(), timestamp);
        }
    }

    private void createMessage(Context context, String address, String body, long timestamp) {
        boolean isClean = false;
        for (Blacklist blacklist : databaseManager.getBlacklist())
            isClean = !address.equalsIgnoreCase(blacklist.getNumber());
        if (isClean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_NAME, address);
            contentValues.put(FIELD_BODY, body);
            contentValues.put(FIELD_DATE, timestamp);
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.insert(CONTENT_PROVIDER_INBOX, contentValues);
            notifyUser(context, address, body);
        }
    }

    private void notifyUser(Context context, String title, String message) {
        InformManager inform = new InformManager(context);
        NotificationCompat.Builder builder = inform.showNotification(title, message);
        inform.getManager().notify((int) (1 + Math.random() * 9999), builder.build());
    }
}
