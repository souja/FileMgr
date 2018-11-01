package com.buyoute.filemanager.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SPHelper
 */
public class SPHelper {

    private static SPHelper instance;
    private SharedPreferences sp;

    public static SPHelper get() {
        if (instance == null) {
            synchronized (SPHelper.class) {
                if (instance == null) instance = new SPHelper();
            }
        }
        return instance;
    }

    public void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return sp.getString(key, "");
    }

    public boolean putString(String key, String value) {
        return sp.edit().putString(key, value).commit();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public boolean putInt(String key, int value) {
        return sp.edit().putInt(key, value).commit();
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public boolean putLong(String key, long value) {
        return sp.edit().putLong(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public void remove(String key) {
        sp.edit().remove(key).commit();
    }

}
