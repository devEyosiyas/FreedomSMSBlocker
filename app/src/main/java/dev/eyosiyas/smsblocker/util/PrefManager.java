package dev.eyosiyas.smsblocker.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PrefManager {
    private final SharedPreferences preferences;
    private final Context context;

    public PrefManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public boolean isNuclearEnabled() {
        return preferences.getBoolean(Constant.NUCLEAR, false);
    }

    public void setNuclear(boolean enable) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.NUCLEAR, enable);
        editor.apply();
    }

    public void setStartsWith(String startsWith) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.STARTS_WITH, startsWith);
        editor.apply();
    }

    public boolean isStartsWithEnabled() {
        return preferences.getString(Constant.STARTS_WITH, "").length() > 1;
    }

    public String getStartsWith() {
        return preferences.getString(Constant.STARTS_WITH, "");
    }

    public void setEndsWith(String endsWith) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.ENDS_WITH, endsWith);
        editor.apply();
    }

    public boolean isEndsWithEnabled() {
        return preferences.getString(Constant.ENDS_WITH, "").length() > 1;
    }

    public String getEndsWith() {
        return preferences.getString(Constant.ENDS_WITH, "");
    }
}
