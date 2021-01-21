package dev.eyosiyas.smsblocker.util

import android.content.Context

class PrefManager constructor(context: Context) {
    private val preferences = context.getSharedPreferences("Fire", Context.MODE_PRIVATE)
    var startsWith: String?
        get() = preferences.getString(Constant.STARTS_WITH, "")
        set(startsWith) = preferences.edit().putString(Constant.STARTS_WITH, startsWith).apply()

    var endsWith: String?
        get() = preferences.getString(Constant.ENDS_WITH, "")
        set(endsWith) = preferences.edit().putString(Constant.ENDS_WITH, endsWith).apply()

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