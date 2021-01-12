package dev.eyosiyas.smsblocker.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.util.*

class PrefManager constructor(context: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var nuclearOption: Boolean
        get() = preferences.getBoolean(Constant.NUCLEAR, false)
        set(value) = preferences.edit().putBoolean(Constant.NUCLEAR, value).apply()

    val isStartsWithEnabled: Boolean
        get() = preferences.getString(Constant.STARTS_WITH, "")!!.length > 1

    var startsWith: String?
        get() = preferences.getString(Constant.STARTS_WITH, "")
        set(startsWith) = preferences.edit().putString(Constant.STARTS_WITH, startsWith).apply()

    val isEndsWithEnabled: Boolean
        get() = preferences.getString(Constant.ENDS_WITH, "")!!.length > 1

    var endsWith: String?
        get() = preferences.getString(Constant.ENDS_WITH, "")
        set(endsWith) = preferences.edit().putString(Constant.ENDS_WITH, endsWith).apply()

    var id: String?
        get() = preferences.getString(Constant.ID, "")
        set(_) = preferences.edit().putString(Constant.ID, UUID.randomUUID().toString()).apply()

    var firstRun: Boolean
        get() = preferences.getBoolean(Constant.FIRST_RUN, true)
        set(value) = preferences.edit().putBoolean(Constant.FIRST_RUN, value).apply()
}