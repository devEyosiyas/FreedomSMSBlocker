package dev.eyosiyas.smsblocker.util

import android.content.Context
import java.util.*

class PrefManager constructor(context: Context) {
    private val preferences = context.getSharedPreferences("Fire", Context.MODE_PRIVATE)

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

    var id: String
        get() = preferences.getString(Constant.ID, "").toString()
        set(_) = preferences.edit().putString(Constant.ID, UUID.randomUUID().toString()).apply()

    var firstRun: Boolean
        get() = preferences.getBoolean(Constant.FIRST_RUN, true)
        set(value) = preferences.edit().putBoolean(Constant.FIRST_RUN, value).apply()

    var locale: String
        get() = preferences.getString(Constant.LANGUAGE, "en")!!
        set(locale) {
            preferences.edit().putString(Constant.LANGUAGE, locale).apply()
        }
    var blockingRule: String
        get() = preferences.getString(Constant.BLOCKING_RULE, "")!!
        set(rule) {
            preferences.edit().putString(Constant.BLOCKING_RULE, rule).apply()
        }
}