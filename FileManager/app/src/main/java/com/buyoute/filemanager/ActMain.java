package com.buyoute.filemanager;

import android.content.Intent;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.buyoute.filemanager.act.ActEmptyDIrs;
import com.buyoute.filemanager.act.ActImages;
import com.buyoute.filemanager.act.ActPrivate;
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
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate() {
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        initScreenParams();

        rvCats.setAdapter(new AdapterCategories(this, cat_id -> {
            switch (cat_id) {
                case 0:
                    NEXT(new Intent(_this, ActImages.class));
                    break;
                case 1:
                    NEXT(new Intent(_this, ActVideos.class));
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    NEXT(new Intent(_this, ActEmptyDIrs.class));
            }
        }));

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.layout_private).setOnClickListener(v -> {
            closeDrawer();
            NEXT(new Intent(_this, ActPrivate.class));
        });

    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
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
