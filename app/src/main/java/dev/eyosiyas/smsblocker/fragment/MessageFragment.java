package dev.eyosiyas.smsblocker.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import dev.eyosiyas.smsblocker.R;
import dev.eyosiyas.smsblocker.adapter.MessageAdapter;
import dev.eyosiyas.smsblocker.model.Message;
import dev.eyosiyas.smsblocker.util.Constant;
import dev.eyosiyas.smsblocker.util.Core;
import dev.eyosiyas.smsblocker.view.SendMessageActivity;

import static dev.eyosiyas.smsblocker.util.Constant.CONTENT_PROVIDER_SMS;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_BODY;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_DATE;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_NAME;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_READ;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_RECEIVED;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_SEEN;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_SENT;
import static dev.eyosiyas.smsblocker.util.Constant.FIELD_TYPE;

public class MessageFragment extends Fragment {
    private RecyclerView smsRecyclerView;

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
        Cursor cursor = getActivity().getContentResolver().query(CONTENT_PROVIDER_SMS, null, null, null, null);
        while (cursor.moveToNext()) {
            if (!list.contains(cursor.getString(cursor.getColumnIndex(FIELD_NAME)))) {
                message = new Message();
                message.setSender(cursor.getString(cursor.getColumnIndex(FIELD_NAME)));
                message.setDisplayName(Core.getDisplayName(getContext(), cursor.getString(cursor.getColumnIndex(FIELD_NAME))));
                list.add(cursor.getString(cursor.getColumnIndex(FIELD_NAME)));
                message.setBody(cursor.getString(cursor.getColumnIndex(FIELD_BODY)));
                message.setRead(cursor.getInt(cursor.getColumnIndex(FIELD_READ)) != 0);
                message.setSeen(cursor.getInt(cursor.getColumnIndex(FIELD_SEEN)) != 0);
                message.setTimestamp(cursor.getLong(cursor.getColumnIndex(FIELD_DATE)));
                message.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE)) == 1 ? FIELD_RECEIVED : FIELD_SENT);
                messages.add(message);
            }
        }
        cursor.close();
        list.clear();
        return messages;
    }

    private void init() {
        MessageAdapter messageAdapter = new MessageAdapter(getContext(), getMessages());
        smsRecyclerView.setAdapter(messageAdapter);
    }
}