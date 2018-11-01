package com.buyoute.filemanager.act;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.adapter.VideoAdapter;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.tools.MGlobal;
import com.buyoute.filemanager.widget.DirAdapter;
import com.buyoute.filemanager.widget.DirLayout;
import com.buyoute.filemanager.widget.MediaBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 选择视频
 * Created by Ydz on 2017/3/16 0016.
 */

public class ActVideos extends ActBase {

    public final static String KEY_COUNT = "key-count";
    @BindView(R.id.media_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.dir_layout)
    DirLayout mDirLayout;
    @BindView(R.id.tv_curDirName)
    TextView tvCurDir;

    private ArrayMap<String, List<String>> mGroupMap; //包含视频的文件夹
    private ArrayMap<String, Integer> sizeMap;//k-视频路径，v-视频大小（M）
    private List<String> allVideoPathList;//所有视频的路径
    public List<String> selectedVideoPathList;//已选择的视频路径
    private List<MediaBean> mDirList;//包含视频的文件夹
    private Handler mHandler;

    public VideoAdapter mAdapter;

    private static ActVideos instance;

    public static ActVideos getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_select_media);
        ButterKnife.bind(this);

        instance = this;

        initVariables();
        initListeners();
        initVideoList();
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }

    private void initVariables() {
        mGroupMap = new ArrayMap<>();
        sizeMap = new ArrayMap<>();
        allVideoPathList = new ArrayList<>();
        selectedVideoPathList = new ArrayList<>();
        mHandler = new Handler();
        mAdapter = new VideoAdapter(_this, allVideoPathList, position ->
                NEXT(new Intent(_this, ActVideoPlayer.class)
                        .putExtra("index", position)));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners() {
        findViewById(R.id.btn_selectDir).setOnClickListener(view -> mDirLayout.notifyVisible());
    }


    private void initVideoList() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast(getResources().getString(R.string.sdcard_nosize));
            return;
        }
        new Thread(() -> {
            ArrayMap<String, Integer> durationMap = new ArrayMap();//key-视频路径，value-视频时长（秒）
            scanData(durationMap, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            scanData(durationMap, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
            mGroupMap.put("所有视频", allVideoPathList);
            mHandler.post(() -> { //扫描视频完成
                mAdapter.setDurationMap(durationMap);
                mAdapter.setVideoPathList(allVideoPathList);
                mDirList = MGlobal.get().subMediaGroup(mGroupMap, true);
                initDirList();
            });
        }).start();
    }

    private void scanData(ArrayMap<String, Integer> durMap, Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED);
        while (mCursor.moveToNext()) {
            // 获取视频路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
            // 获取视频时长
            long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            //视频大小(KB)
            long size = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            sizeMap.put(path, (int) (size / 1024));
            int s = (int) (duration / 1000); //秒
            durMap.put(path, s);
            allVideoPathList.add(path);
            // 获取该视频的父路径名
            String dirName = new File(path).getParentFile().getName();
            // 根据父路径名将视频放入到mGruopMap中
            if (!mGroupMap.containsKey(dirName)) {
                List<String> childList = new ArrayList<>();
                childList.add(path);
                mGroupMap.put(dirName, childList);
            } else {
                mGroupMap.get(dirName).add(path);
            }
        }
        mCursor.close();
    }

    private void initDirList() {
        DirAdapter dirAdapter = new DirAdapter(_this, mDirList, true, mediaBean -> {
            String key = mediaBean.getFolderName();
            tvCurDir.setText(key);
            mAdapter.setVideoPathList(mGroupMap.get(key));
            mDirLayout.dismiss();
        });
        mDirLayout.setAdapter(dirAdapter);
    }


    public ArrayMap<String, Integer> getSizeMap() {
        return sizeMap;
    }


    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
