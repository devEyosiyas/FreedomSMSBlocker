package dev.gdgaddis.codenightsmsblocker.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static dev.gdgaddis.codenightsmsblocker.util.Constant.DATE_PATTERN;
import static dev.gdgaddis.codenightsmsblocker.util.Constant.DATE_TIME_PATTERN;
import static dev.gdgaddis.codenightsmsblocker.util.Constant.NUMBER;
import static dev.gdgaddis.codenightsmsblocker.util.Constant.READ_CONTACTS;
import static dev.gdgaddis.codenightsmsblocker.util.Constant.REQUEST_SETTING;
import static dev.gdgaddis.codenightsmsblocker.util.Constant.TIME_PATTERN;

public class Core {
    public static String getReadableTime(long timestamp) {
        String currentDate = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String date = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH).format(new Date(timestamp));
        if (date.equals(currentDate))
            return new SimpleDateFormat(TIME_PATTERN, Locale.ENGLISH).format(new Date(timestamp));
        else
            return new SimpleDateFormat(DATE_TIME_PATTERN, Locale.ENGLISH).format(new Date(timestamp));
    }

    public static void permissionDenied(final Activity activity, String title, String message, final boolean forced) {
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(activity);
        dialog.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settings(activity);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (forced)
                            activity.finish();
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private static void settings(Activity context) {
        if (context != null) {
            Toast.makeText(context, "Allow Permission form settings", Toast.LENGTH_SHORT).show();
            context.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", context.getApplicationInfo().packageName, null)), REQUEST_SETTING);
        }
    }

    public static String getDisplayName(Context context, String number) {
        if (ContextCompat.checkSelfPermission(context, READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            return number;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst())
                    number = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            } finally {
                cursor.close();
            }
        }
        return number;
    }

    public static boolean checkContactsPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getContactPhone(@NonNull Context context, @Nullable Intent data) {
        String number = null;
        assert data != null;
        Uri uri = data.getData();
        assert uri != null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                number = cursor.getString(cursor.getColumnIndex(NUMBER));
            }
            cursor.close();
        }
        if (number != null)
            return number;
        else
            return "";
    }
}
