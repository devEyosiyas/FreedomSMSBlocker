package dev.gdgaddis.codenightsmsblocker.view;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.gdgaddis.codenightsmsblocker.R;
import dev.gdgaddis.codenightsmsblocker.adapter.MessageListAdapter;
import dev.gdgaddis.codenightsmsblocker.model.Message;
import dev.gdgaddis.codenightsmsblocker.util.Core;

public class DetailSmsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sms);

        String KEY = getIntent().getStringExtra("KEY");

        if (KEY == null)
            onBackPressed();
        messageListAdapter = new MessageListAdapter(this, getMessages(KEY));

        recyclerView = findViewById(R.id.detailMessageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageListAdapter);
    }

    public List<Message> getMessages(String search) {
        List<Message> messages = new ArrayList<>();
        Message message;
        Uri messageUri = Uri.parse("content://sms/");

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, "address=?", new String[]{search}, null);

//        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
        int totalSMS = cursor.getCount();
        Toast.makeText(this, "Total message count " + totalSMS, Toast.LENGTH_SHORT).show();
        while (cursor.moveToNext()) {
            message = new Message();
            message.setSender(cursor.getString(cursor.getColumnIndex("address")));
            message.setDisplayName(Core.getDisplayName(this, cursor.getString(cursor.getColumnIndex("address"))));
            message.setBody(cursor.getString(cursor.getColumnIndex("body")));
            message.setRead(cursor.getInt(cursor.getColumnIndex("read")) != 0);
            message.setSeen(cursor.getInt(cursor.getColumnIndex("seen")) != 0);
            message.setTimestamp(cursor.getLong(cursor.getColumnIndex("date")));
            message.setType(cursor.getInt(cursor.getColumnIndex("type")) == 1 ? "received" : "sent");
            messages.add(message);
        }
        cursor.close();
        return messages;
    }
}