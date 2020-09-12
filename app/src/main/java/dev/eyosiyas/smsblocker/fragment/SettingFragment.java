package dev.eyosiyas.smsblocker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.eyosiyas.smsblocker.R;
import dev.eyosiyas.smsblocker.adapter.KeywordAdapter;
import dev.eyosiyas.smsblocker.database.DatabaseManager;
import dev.eyosiyas.smsblocker.util.PrefManager;

public class SettingFragment extends Fragment {
    private DatabaseManager databaseManager;
    private RecyclerView recyclerView;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new DatabaseManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        final EditText startsWith = view.findViewById(R.id.editStartsWith);
        final EditText endsWith = view.findViewById(R.id.editEndsWith);
        Button keyword = view.findViewById(R.id.btnKeyword);
        Button changeRule = view.findViewById(R.id.btnChangeRule);
        SwitchCompat nuclearSwitch = view.findViewById(R.id.nuclearOption);
        final PrefManager manager = new PrefManager(getContext());
        nuclearSwitch.setChecked(manager.isNuclearEnabled());
        startsWith.setText(manager.getStartsWith());
        endsWith.setText(manager.getEndsWith());
        changeRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startsWith.getText().length() > 0) {
                    manager.setStartsWith(startsWith.getText().toString());
                    Toast.makeText(getContext(), String.format("Now blocking every number that starts with %s", startsWith.getText().toString()), Toast.LENGTH_LONG).show();
                } else {
                    manager.setStartsWith("");
                    Toast.makeText(getContext(), "Starts with blocking disabled ", Toast.LENGTH_LONG).show();
                }
                if (endsWith.getText().length() > 0) {
                    manager.setEndsWith(endsWith.getText().toString());
                    Toast.makeText(getContext(), String.format("Now blocking every number that ends with %s", endsWith.getText().toString()), Toast.LENGTH_LONG).show();
                } else {
                    manager.setEndsWith("");
                    Toast.makeText(getContext(), "Ends with blocking disabled ", Toast.LENGTH_LONG).show();
                }
            }
        });

        nuclearSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    manager.setNuclear(true);
                    Toast.makeText(getContext(), "Nuclear option enabled!\nNow blocking EVERY 3 & 4 digit numbers.", Toast.LENGTH_LONG).show();
                } else
                    manager.setNuclear(false);
            }
        });

        keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertKeywordUI();
            }
        });
        return view;
    }

    private void insertKeywordUI() {
        final View view = View.inflate(getContext(), R.layout.keyword_management, null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        final DatabaseManager databaseManager = new DatabaseManager(getContext());
        final EditText editKeyword = view.findViewById(R.id.editKeyword);
        final Button insert = view.findViewById(R.id.btnInsertUpdateKeyword);
        recyclerView = view.findViewById(R.id.keywordRecyclerView);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editKeyword.getText().toString().length() > 2) {
                    databaseManager.insertKeyword(editKeyword.getText().toString());
                    Toast.makeText(getContext(), editKeyword.getText().toString() + " saved to the database.", Toast.LENGTH_SHORT).show();
                    editKeyword.setText("");
                    refreshKeyword();
                } else
                    Toast.makeText(getContext(), "Please use a keyword of length more than three.", Toast.LENGTH_SHORT).show();
            }
        });
        refreshKeyword();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dialog.setView(view);
        dialog.show();
    }

    private void refreshKeyword() {
        KeywordAdapter keywordAdapter = new KeywordAdapter(databaseManager.getKeywords(), getContext());
        recyclerView.setAdapter(keywordAdapter);
    }
}