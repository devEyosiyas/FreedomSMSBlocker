package dev.gdgaddis.codenightsmsblocker.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dev.gdgaddis.codenightsmsblocker.R;
import dev.gdgaddis.codenightsmsblocker.adapter.BlacklistAdapter;
import dev.gdgaddis.codenightsmsblocker.database.DatabaseManager;
import dev.gdgaddis.codenightsmsblocker.model.Blacklist;

public class BlockFragment extends Fragment {
    private RecyclerView recyclerView;
    private BlacklistAdapter adapter;
    private List<Blacklist> blacklists;

    public BlockFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block, container, false);

        blacklists = new ArrayList<>();

        recyclerView = view.findViewById(R.id.blacklistRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadBlacklist();

        FloatingActionButton button = view.findViewById(R.id.fabSendMessage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blacklistManager();
            }
        });
        return view;
    }

    private void loadBlacklist() {
        DatabaseManager manager = new DatabaseManager(getContext());
        adapter = new BlacklistAdapter(getContext(), manager.getBlacklist());
        recyclerView.setAdapter(adapter);
    }

    private void blacklistManager() {
        final View view = View.inflate(getContext(), R.layout.blacklist_managment, null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        final EditText number = view.findViewById(R.id.inputBlacklistNumber);
        final Button operation = view.findViewById(R.id.btnInsertUpdate);
        dialog.setView(view);
        operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager manager = new DatabaseManager(getContext());
                manager.insert(number.getText().toString());
                Toast.makeText(getContext(), number.getText() + " added to blacklist.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadBlacklist();
            }
        });
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                operation.setEnabled(number.getText().length() > 2);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog.show();
    }
}