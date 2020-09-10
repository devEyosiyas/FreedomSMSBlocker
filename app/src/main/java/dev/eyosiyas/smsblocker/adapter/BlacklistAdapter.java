package dev.eyosiyas.smsblocker.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import dev.eyosiyas.smsblocker.R;
import dev.eyosiyas.smsblocker.database.DatabaseManager;
import dev.eyosiyas.smsblocker.model.Blacklist;
import dev.eyosiyas.smsblocker.util.Core;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.ViewHolder> {
    private Context context;
    private List<Blacklist> blacklists;
    private DatabaseManager manager;


    public BlacklistAdapter(Context context, List<Blacklist> blacklists) {
        this.context = context;
        this.blacklists = blacklists;
        manager = new DatabaseManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blacklist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Blacklist blacklist = blacklists.get(position);
        holder.number.setText(blacklist.getNumber());
        holder.time.setText(Core.getReadableTime(blacklist.getTimestamp()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(context);
                dialog.setTitle("Remove from Blacklist.")
                        .setCancelable(false)
                        .setMessage(String.format(Locale.US, "Do you want to remove %s from the Blacklist?", blacklist.getNumber()))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.remove(blacklist.getId());
                                blacklists.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, blacklist.getNumber() + " removed successfully.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blacklistManager(blacklist.getId(), position);
            }
        });
    }

    private void blacklistManager(final int id, final int position) {
        final View view = View.inflate(context, R.layout.blacklist_managment, null);
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        final EditText number = view.findViewById(R.id.inputBlacklistNumber);
        final Button operation = view.findViewById(R.id.btnInsertUpdate);
        final Button contact = view.findViewById(R.id.btnSelectNumber);
        final DatabaseManager manager = new DatabaseManager(context);
        operation.setText(R.string.update);
        number.setText(id > 0 ? manager.getBlacklist(id).getNumber() : "");
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
//                if (Core.checkContactsPermission(activity))
//                    activity.startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_SELECT_CONTACT);
//                else
//                    ActivityCompat.requestPermissions(activity, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
            }
        });
        dialog.setView(view);
        operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.update(id, number.getText().toString());
                blacklists.set(position, manager.getBlacklist(id));
                notifyItemChanged(position);
                Toast.makeText(context, "Blacklist record updated.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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

    @Override
    public int getItemCount() {
        return blacklists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete, edit;
        TextView number, time;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.txtBlacklistNumber);
            time = itemView.findViewById(R.id.txtBlacklistTimestamp);
            delete = itemView.findViewById(R.id.imgDeleteNumber);
            edit = itemView.findViewById(R.id.imgEditNumber);
        }
    }


}


