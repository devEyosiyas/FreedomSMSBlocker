package dev.eyosiyas.smsblocker.view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dev.eyosiyas.smsblocker.R;
import dev.eyosiyas.smsblocker.util.Constant;
import dev.eyosiyas.smsblocker.util.Core;

import static dev.eyosiyas.smsblocker.util.Constant.FILTER_SMS_DELIVERED;
import static dev.eyosiyas.smsblocker.util.Constant.FILTER_SMS_SENT;
import static dev.eyosiyas.smsblocker.util.Constant.PERMISSION_REQUEST_READ_CONTACTS;
import static dev.eyosiyas.smsblocker.util.Constant.READ_CONTACTS;
import static dev.eyosiyas.smsblocker.util.Constant.REQUEST_SELECT_CONTACT;

public class SendMessageActivity extends AppCompatActivity {
    private EditText receiver, message;
    private Button send;

    private final BroadcastReceiver sentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Message sent successfully.", Toast.LENGTH_SHORT).show();
                    resetUI();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "RESULT_ERROR_GENERIC_FAILURE", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "ERROR_NO_SERVICE", Toast.LENGTH_SHORT).show();

                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "ERROR_NULL_PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "ERROR_RADIO_OFF", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final BroadcastReceiver deliveredBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Message delivered to recipient.", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "Message not received by recipient.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sentBroadcastReceiver, new IntentFilter(FILTER_SMS_SENT));
        registerReceiver(deliveredBroadcastReceiver, new IntentFilter(FILTER_SMS_DELIVERED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(sentBroadcastReceiver);
        unregisterReceiver(deliveredBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        receiver = findViewById(R.id.editReceiver);
        message = findViewById(R.id.editMessageBox);
        send = findViewById(R.id.btnSendMessage);
        Button contact = findViewById(R.id.btnSelectContact);

        receiver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Core.checkContactsPermission(SendMessageActivity.this))
                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_SELECT_CONTACT);
                else
                    ActivityCompat.requestPermissions(SendMessageActivity.this, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void validation() {
        send.setEnabled(receiver.getText().length() > 2 && message.getText().length() > 0);
    }

    private void sendMessage() {
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(FILTER_SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(FILTER_SMS_DELIVERED), 0);

        if (ContextCompat.checkSelfPermission(SendMessageActivity.this, Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(receiver.getText().toString(), null, message.getText().toString(), sentPendingIntent, deliveredPendingIntent);
        } else
            Toast.makeText(SendMessageActivity.this, "Permission missing.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == Activity.RESULT_OK)
            receiver.setText(Core.getContactPhone(SendMessageActivity.this, data));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), PERMISSION_REQUEST_READ_CONTACTS);
        }
    }

    private void resetUI() {
        receiver.setText("");
        message.setText("");
        send.setEnabled(false);
    }
}