package dev.eyosiyas.smsblocker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.eyosiyas.smsblocker.dao.*
import dev.eyosiyas.smsblocker.model.*
import dev.eyosiyas.smsblocker.util.PrefManager
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Database(entities = [Keyword::class, Blacklist::class, Blocked::class, Message::class, Whitelist::class], version = 1, exportSchema = false)
abstract class Storage : RoomDatabase() {
    abstract fun keywordDao(): KeywordDao
    abstract fun blacklistDao(): BlacklistDao
    abstract fun blockedDao(): BlockedDao
    abstract fun whitelistDao(): WhitelistDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var instance: Storage? = null

        fun database(context: Context): Storage {
            return instance ?: synchronized(this) {
                return Room.databaseBuilder(context.applicationContext, Storage::class.java, "Storage").openHelperFactory(SupportFactory(SQLiteDatabase.getBytes(PrefManager(context).id.toCharArray()))).build()
            }
        }
    }
}