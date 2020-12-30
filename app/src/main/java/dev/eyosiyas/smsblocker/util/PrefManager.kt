package dev.eyosiyas.smsblocker.util

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.preference.PreferenceManager

class PrefManager constructor(private val context: Context?) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val isNuclearEnabled: Boolean
        get() {
            return preferences.getBoolean(Constant.NUCLEAR, false)
        }

    fun setNuclear(enable: Boolean) {
        val editor: Editor = preferences.edit()
        editor.putBoolean(Constant.NUCLEAR, enable)
        editor.apply()
    }

    val isStartsWithEnabled: Boolean
        get() {
            return preferences.getString(Constant.STARTS_WITH, "")!!.length > 1
        }
    var startsWith: String?
        get() {
            return preferences.getString(Constant.STARTS_WITH, "")
        }
        set(startsWith) {
            val editor: Editor = preferences.edit()
            editor.putString(Constant.STARTS_WITH, startsWith)
            editor.apply()
        }
    val isEndsWithEnabled: Boolean
        get() {
            return preferences.getString(Constant.ENDS_WITH, "")!!.length > 1
        }
    var endsWith: String?
        get() {
            return preferences.getString(Constant.ENDS_WITH, "")
        }
        set(endsWith) {
            val editor: Editor = preferences.edit()
            editor.putString(Constant.ENDS_WITH, endsWith)
            editor.apply()
        }

}