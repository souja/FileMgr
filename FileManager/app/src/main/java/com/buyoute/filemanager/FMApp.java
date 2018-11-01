package com.buyoute.filemanager;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;

public class FMApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
