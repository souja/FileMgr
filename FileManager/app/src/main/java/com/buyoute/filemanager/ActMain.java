package com.buyoute.filemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.buyoute.filemanager.act.ActImages;
import com.buyoute.filemanager.act.ActVideos;
import com.buyoute.filemanager.adapter.AdapterCategories;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.tools.LogUtil;
import com.buyoute.filemanager.tools.MGlobal;
import com.buyoute.filemanager.tools.MTool;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActMain extends ActBase {
    @BindView(R.id.rv_cats)
    RecyclerView rvCats;


    @Override
    public void onCreate() {
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);

        initScreenParams();

        rvCats.setAdapter(new AdapterCategories(this, str -> {
            switch (str) {
                case "图片":
                    NEXT(new Intent(_this, ActImages.class));
                    break;
                case "视频":
                    NEXT(new Intent(_this, ActVideos.class));
                    break;
            }
        }));

        if (PermissionChecker.checkSelfPermission(_this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_this, new String[]{Manifest.permission.CAMERA},
                    1122);
        }
    }

    //初始化屏幕及设备参数
    private synchronized void initScreenParams() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        MGlobal.get().setDpi(displayMetrics.densityDpi);
        MGlobal.get().initScreenParam(displayMetrics);

        MGlobal.get().setKeybordHeight(MTool.getNavigationBarHeight(_this));

        String model = Build.MODEL;
        String carrier = Build.MANUFACTURER;
        LogUtil.e("手机型号/厂商：" + model + "," + carrier);

//        int statusBarHeight = FMApp.getStatusBarHeight(this);
//        FMApp.setStatusBarHeight(statusBarHeight);
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }

}
