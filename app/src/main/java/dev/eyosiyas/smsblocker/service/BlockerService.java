package dev.eyosiyas.smsblocker.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.eyosiyas.smsblocker.database.DatabaseManager;
import dev.eyosiyas.smsblocker.model.Blacklist;
import dev.eyosiyas.smsblocker.model.Keyword;
import dev.eyosiyas.smsblocker.util.Core;
import dev.eyosiyas.smsblocker.util.InformManager;
import dev.eyosiyas.smsblocker.util.PrefManager;

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
        PrefManager manager = new PrefManager(context);
        boolean isClean = true;
        for (Blacklist blacklist : databaseManager.getBlacklist()) {
            if (address.equalsIgnoreCase(blacklist.getNumber())) {
                isClean = false;
                break;
            } else
                isClean = true;
        }

        if (isClean && manager.isNuclearEnabled()) {
            Pattern nuclearPattern = Pattern.compile("(?<!\\d)\\d{3,4}(?!\\d)");
            Matcher matcher = nuclearPattern.matcher(address);
            isClean = !matcher.find();
        }

        if (isClean && manager.isStartsWithEnabled()) {
            Pattern startsWithPattern = Pattern.compile(String.format("^%s", manager.getStartsWith()));
            Matcher matcher = startsWithPattern.matcher(address);
            isClean = !matcher.find();
        }

        if (isClean && manager.isEndsWithEnabled()) {
            Pattern endsWithPattern = Pattern.compile(String.format("%s$", manager.getEndsWith()));
            Matcher matcher = endsWithPattern.matcher(address);
            isClean = !matcher.find();
        }

        if (isClean && databaseManager.getKeywordsCount() > 0) {
            for (Keyword keyword : databaseManager.getKeywords()) {
                if (body.contains(keyword.getKeyword())) {
                    isClean = false;
                    break;
                } else {
                    isClean = true;
                }
            }
        }

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

    private void notifyUser(Context context, String number, String message) {
        InformManager inform = new InformManager(context);
        NotificationCompat.Builder builder = inform.showNotification(Core.getDisplayName(context, number), message);
        inform.getManager().notify((int) (1 + Math.random() * 9999), builder.build());
    }
}
