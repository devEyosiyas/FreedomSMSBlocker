package dev.gdgaddis.codenightsmsblocker.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dev.gdgaddis.codenightsmsblocker.R;
import dev.gdgaddis.codenightsmsblocker.adapter.MessageAdapter;
import dev.gdgaddis.codenightsmsblocker.model.Message;
import dev.gdgaddis.codenightsmsblocker.util.Constant;
import dev.gdgaddis.codenightsmsblocker.util.Core;
import dev.gdgaddis.codenightsmsblocker.view.SendMessageActivity;

public class MessageFragment extends Fragment {
    private RecyclerView smsRecyclerView;
    private MessageAdapter messageAdapter;

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        FloatingActionButton sendSMSActionButton = view.findViewById(R.id.fabSendMessage);
        smsRecyclerView = view.findViewById(R.id.smsListRecycler);
        sendSMSActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Constant.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                    startActivity(new Intent(getContext(), SendMessageActivity.class));
                else
                    Toast.makeText(getContext(), "You need permission", Toast.LENGTH_SHORT).show();
            }
        });

        smsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        init();
        return view;
    }

    public List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        List<String> list = new ArrayList<>();
        Message message;
        Uri messageUri = Uri.parse("content://sms/");
        Cursor cursor = getActivity().getContentResolver().query(messageUri, null, null, null, null);
        int totalSMS = cursor.getCount();
        Toast.makeText(getContext(), "Total message count " + totalSMS, Toast.LENGTH_SHORT).show();
        while (cursor.moveToNext()) {
            if (!list.contains(cursor.getString(cursor.getColumnIndex("address")))) {
                message = new Message();
                message.setSender(cursor.getString(cursor.getColumnIndex("address")));
                message.setDisplayName(Core.getDisplayName(getContext(), cursor.getString(cursor.getColumnIndex("address"))));
                list.add(cursor.getString(cursor.getColumnIndex("address")));
                message.setBody(cursor.getString(cursor.getColumnIndex("body")));
                message.setRead(cursor.getInt(cursor.getColumnIndex("read")) != 0);
                message.setSeen(cursor.getInt(cursor.getColumnIndex("seen")) != 0);
                message.setTimestamp(cursor.getLong(cursor.getColumnIndex("date")));
                message.setType(cursor.getInt(cursor.getColumnIndex("type")) == 1 ? "received" : "sent");
                messages.add(message);
            }
        }
        cursor.close();
        list.clear();
        return messages;
    }

    private void init() {
        messageAdapter = new MessageAdapter(getContext(), getMessages());
        smsRecyclerView.setAdapter(messageAdapter);
    }
}