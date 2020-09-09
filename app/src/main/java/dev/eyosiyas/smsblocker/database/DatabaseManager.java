package dev.eyosiyas.smsblocker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.eyosiyas.smsblocker.model.Blacklist;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseManager";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "BlacklistDB";
    private static final String TABLE_BLACKLIST = "Blacklist";
    private static final String COLUMN_NUMBER = "Number";
    private static final String COLUMN_TIMESTAMP = "Timestamp";
    private static final String COLUMN_SOURCE = "Source";

    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Blacklist (ID INTEGER PRIMARY KEY AUTOINCREMENT, Number TEXT, Timestamp INTEGER, Source TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Blacklist");
        onCreate(db);
    }

    public void insert(String number) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_SOURCE, "local");
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        database.insert(TABLE_BLACKLIST, null, values);
        Log.d(TAG, "insert: data written");
        Log.d(TAG, "insert() returned: " + values.toString());
        database.close();
    }

    public void update(int id, String number, long timestamp, String source) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_SOURCE, source);
        values.put(COLUMN_TIMESTAMP, timestamp);
        database.update(TABLE_BLACKLIST, values, "ID=" + id, null);
        database.close();
    }

    public void remove(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_BLACKLIST, "ID=" + id, null);
        database.close();
    }

    public void remove() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_BLACKLIST, null, null);
        database.close();
    }

    public Blacklist getBlacklist(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM Blacklist WHERE ID=" + id, null);
        Blacklist blacklist = null;
        if (cursor != null) {
            cursor.moveToFirst();
            blacklist = new Blacklist();
            blacklist.setId(cursor.getInt(0));
            blacklist.setNumber(cursor.getString(1));
            blacklist.setTimestamp(cursor.getLong(2));
            cursor.close();
        }
        Log.d(TAG, "getBlacklist() returned: " + blacklist);
        return blacklist;
    }

    public List<Blacklist> getBlacklist() {
        List<Blacklist> blacklists = new ArrayList<>();
        Blacklist blacklist;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM Blacklist", null);
        if (cursor.moveToFirst()) {
            do {
                blacklist = new Blacklist();
                blacklist.setId(cursor.getInt(0));
                blacklist.setNumber(cursor.getString(1));
                blacklist.setTimestamp(cursor.getLong(2));
                blacklists.add(blacklist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return blacklists;
    }

    public int getCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT count(*) FROM Blacklist", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

}