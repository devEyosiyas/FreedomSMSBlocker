package dev.eyosiyas.smsblocker.adapter;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import dev.eyosiyas.smsblocker.R;
import dev.eyosiyas.smsblocker.database.DatabaseManager;
import dev.eyosiyas.smsblocker.model.Keyword;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.VH> {
    private Context context;
    private List<Keyword> keywordList;
    DatabaseManager manager;

    public KeywordAdapter(List<Keyword> keywordList, Context context) {
        this.keywordList = keywordList;
        this.context = context;
        manager = new DatabaseManager(context);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        final Keyword keyword = keywordList.get(position);
        holder.keyword.setText(keyword.getKeyword());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(context);
                dialog.setTitle(String.format("Remove %s", keyword.getKeyword()))
                        .setCancelable(false)
                        .setMessage(String.format(Locale.US, "Are you sure you want to remove %s?", keyword.getKeyword()))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.removeKeyword(keyword.getId());
                                keywordList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, keyword.getKeyword() + " removed successfully.", Toast.LENGTH_SHORT).show();
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
                updateKeywordUI(keyword.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private TextView keyword;
        private ImageView delete, edit;

        public VH(@NonNull View itemView) {
            super(itemView);
            keyword = itemView.findViewById(R.id.txtKeyword);
            delete = itemView.findViewById(R.id.deleteKeyword);
            edit = itemView.findViewById(R.id.editKeyword);
        }
    }

    private void updateKeywordUI(final int id, final int position) {
        final View view = View.inflate(context, R.layout.keyword_management, null);
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        final DatabaseManager databaseManager = new DatabaseManager(context);
        final EditText editKeyword = view.findViewById(R.id.editKeyword);
        final Button update = view.findViewById(R.id.btnInsertUpdateKeyword);
        RecyclerView recyclerView = view.findViewById(R.id.keywordRecyclerView);
        KeywordAdapter keywordAdapter;
        keywordAdapter = new KeywordAdapter(databaseManager.getKeywords(), context);
        update.setText(context.getString(R.string.update));
        editKeyword.setText(keywordList.get(position).getKeyword());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editKeyword.getText().toString().length() > 2) {
                    databaseManager.updateKeyword(id, editKeyword.getText().toString());
                    Toast.makeText(context, "Update was successful!", Toast.LENGTH_SHORT).show();
                    editKeyword.setText("");
                    keywordList.set(position, manager.getKeyword(id));
                    notifyItemChanged(position);
                    notifyItemChanged(position, keywordList.get(position));
                } else
                    Toast.makeText(context, "Please use a keyword of length more than three.", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(keywordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        dialog.setView(view);
        dialog.show();
    }
}
