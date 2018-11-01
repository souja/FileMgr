package com.buyoute.filemanager;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;
import com.buyoute.filemanager.tools.SPHelper;

public class FMApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SPHelper.get().init(this, getPackageName());
        Utils.init(this);
    }
}
