package dev.eyosiyas.smsblocker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import dev.eyosiyas.smsblocker.model.Blacklist;
import dev.eyosiyas.smsblocker.model.Keyword;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "BlacklistDB";
    private static final String TABLE_BLACKLIST = "Blacklist";
    private static final String TABLE_KEYWORD = "Keyword";
    private static final String COLUMN_NUMBER = "Number";
    private static final String COLUMN_TIMESTAMP = "Timestamp";
    private static final String COLUMN_SOURCE = "Source";
    private static final String COLUMN_CONTAINS = "Contains";

    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Blacklist (ID INTEGER PRIMARY KEY AUTOINCREMENT, Number TEXT, Timestamp INTEGER, Source TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Keyword (ID INTEGER PRIMARY KEY AUTOINCREMENT, Contains TEXT)");
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
        database.close();
    }

    public void insertKeyword(String keyword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTAINS, keyword);
        db.insert(TABLE_KEYWORD, null, contentValues);
        db.close();
    }

    public void update(int id, String number) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_SOURCE, "local");
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        database.update(TABLE_BLACKLIST, values, "ID=" + id, null);
        database.close();
    }

    public void updateKeyword(int id, String keyword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTAINS, keyword);
        db.update(TABLE_KEYWORD, values, "ID=" + id, null);
        db.close();
    }

    public void remove(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_BLACKLIST, "ID=" + id, null);
        database.close();
    }

    public void removeKeyword(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_KEYWORD, "ID=" + id, null);
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

    public List<Keyword> getKeywords() {
        List<Keyword> keywordList = new ArrayList<>();
        Keyword keyword;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM Keyword", null);
        if (cursor.moveToFirst()) {
            do {
                keyword = new Keyword();
                keyword.setId(cursor.getInt(0));
                keyword.setKeyword(cursor.getString(1));
                keywordList.add(keyword);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return keywordList;
    }

    public Keyword getKeyword(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM keyword WHERE ID=" + id, null);
        Keyword keyword = null;
        if (cursor != null) {
            cursor.moveToFirst();
            keyword = new Keyword();
            keyword.setId(cursor.getInt(0));
            keyword.setKeyword(cursor.getString(1));
            cursor.close();
        }
        return keyword;
    }

    public int getCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT count(*) FROM Blacklist", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getKeywordsCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT count(*) FROM Keyword", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}