package dev.gdgaddis.codenightsmsblocker.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import dev.gdgaddis.codenightsmsblocker.R;
import dev.gdgaddis.codenightsmsblocker.database.DatabaseManager;
import dev.gdgaddis.codenightsmsblocker.model.Blacklist;
import dev.gdgaddis.codenightsmsblocker.util.Core;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.ViewHolder> {
    private Context context;
    private List<Blacklist> blacklists;

    public BlacklistAdapter(Context context, List<Blacklist> blacklists) {
        this.context = context;
        this.blacklists = blacklists;
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
                                DatabaseManager manager = new DatabaseManager(context);
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
    }

    @Override
    public int getItemCount() {
        return blacklists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        TextView number, time;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.txtBlacklistNumber);
            time = itemView.findViewById(R.id.txtBlacklistTimestamp);
            delete = itemView.findViewById(R.id.imgDeleteNumber);
        }
    }
}


