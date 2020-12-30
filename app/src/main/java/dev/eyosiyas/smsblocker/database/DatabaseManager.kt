package dev.eyosiyas.smsblocker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.eyosiyas.smsblocker.model.Blacklist
import dev.eyosiyas.smsblocker.model.Keyword
import java.util.*

class DatabaseManager constructor(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Blacklist (ID INTEGER PRIMARY KEY AUTOINCREMENT, Number TEXT, Timestamp INTEGER, Source TEXT);")
        db.execSQL("CREATE TABLE IF NOT EXISTS Keyword (ID INTEGER PRIMARY KEY AUTOINCREMENT, Contains TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Blacklist")
        onCreate(db)
    }

    fun insert(number: String?) {
        val database: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NUMBER, number)
        values.put(COLUMN_SOURCE, "local")
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis())
        database.insert(TABLE_BLACKLIST, null, values)
        database.close()
    }

    fun insertKeyword(keyword: String?) {
        val db: SQLiteDatabase = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_CONTAINS, keyword)
        db.insert(TABLE_KEYWORD, null, contentValues)
        db.close()
    }

    fun update(id: Int, number: String?) {
        val database: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NUMBER, number)
        values.put(COLUMN_SOURCE, "local")
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis())
        database.update(TABLE_BLACKLIST, values, "ID=$id", null)
        database.close()
    }

    fun updateKeyword(id: Int, keyword: String?) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTAINS, keyword)
        db.update(TABLE_KEYWORD, values, "ID=$id", null)
        db.close()
    }

    fun remove(id: Int) {
        val database: SQLiteDatabase = writableDatabase
        database.delete(TABLE_BLACKLIST, "ID=$id", null)
        database.close()
    }

    fun removeKeyword(id: Int) {
        val database: SQLiteDatabase = writableDatabase
        database.delete(TABLE_KEYWORD, "ID=$id", null)
        database.close()
    }

    fun remove() {
        val database: SQLiteDatabase = writableDatabase
        database.delete(TABLE_BLACKLIST, null, null)
        database.close()
    }

    fun blacklist(id: Int): Blacklist? {
        val database: SQLiteDatabase = readableDatabase
        val cursor: Cursor? = database.rawQuery("SELECT * FROM Blacklist WHERE ID=$id", null)
        var blacklist: Blacklist? = null
        if (cursor != null) {
            cursor.moveToFirst()
            blacklist = Blacklist(id = cursor.getInt(0), number = cursor.getString(1), timestamp = cursor.getLong(2))
            cursor.close()
        }
        return blacklist
    }

    val blacklist: MutableList<Blacklist>
        get() {
            val blacklists: MutableList<Blacklist> = ArrayList()
            val database: SQLiteDatabase = readableDatabase
            val cursor: Cursor = database.rawQuery("SELECT * FROM Blacklist", null)
            if (cursor.moveToFirst()) {
                do {
                    blacklists.add(Blacklist(id = cursor.getInt(0), number = cursor.getString(1), timestamp = cursor.getLong(2)))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return blacklists
        }
    val keywords: MutableList<Keyword>
        get() {
            val keywordList: MutableList<Keyword> = ArrayList()
            val database: SQLiteDatabase = readableDatabase
            val cursor: Cursor = database.rawQuery("SELECT * FROM Keyword", null)
            if (cursor.moveToFirst()) {
                do {
                    keywordList.add(Keyword(id = cursor.getInt(0), keyword = cursor.getString(1)))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return keywordList
        }

    fun getKeyword(id: Int): Keyword? {
        val database: SQLiteDatabase = readableDatabase
        val cursor: Cursor? = database.rawQuery("SELECT * FROM keyword WHERE ID=$id", null)
        var keyword: Keyword? = null
        if (cursor != null) {
            cursor.moveToFirst()
            keyword = Keyword(id = cursor.getInt(0), keyword = cursor.getString(1))
            cursor.close()
        }
        return keyword
    }

    val count: Int
        get() {
            val database: SQLiteDatabase = readableDatabase
            val cursor: Cursor = database.rawQuery("SELECT count(*) FROM Blacklist", null)
            cursor.moveToFirst()
            val count: Int = cursor.getInt(0)
            cursor.close()
            return count
        }
    val keywordsCount: Int
        get() {
            val database: SQLiteDatabase = readableDatabase
            val cursor: Cursor = database.rawQuery("SELECT count(*) FROM Keyword", null)
            cursor.moveToFirst()
            val count: Int = cursor.getInt(0)
            cursor.close()
            return count
        }

    companion object {
        private const val DB_VERSION: Int = 1
        private const val DB_NAME: String = "BlacklistDB"
        private const val TABLE_BLACKLIST: String = "Blacklist"
        private const val TABLE_KEYWORD: String = "Keyword"
        private const val COLUMN_NUMBER: String = "Number"
        private const val COLUMN_TIMESTAMP: String = "Timestamp"
        private const val COLUMN_SOURCE: String = "Source"
        private const val COLUMN_CONTAINS: String = "Contains"
    }
}