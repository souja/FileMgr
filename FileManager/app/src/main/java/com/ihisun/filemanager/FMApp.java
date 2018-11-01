package com.ihisun.filemanager;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class FMApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
