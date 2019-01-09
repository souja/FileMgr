package com.buyoute.filemanager.act;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.adapter.VideoAdapter;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.tools.LogUtil;
import com.buyoute.filemanager.tools.MConstant;
import com.buyoute.filemanager.tools.MGlobal;
import com.buyoute.filemanager.tools.MTool;
import com.buyoute.filemanager.tools.SPHelper;
import com.buyoute.filemanager.widget.DirAdapter;
import com.buyoute.filemanager.widget.DirLayout;
import com.buyoute.filemanager.widget.MediaBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 音频
 * Created by Ydz on 2017/3/16 0016.
 */

public class ActAudios extends ActBase {

    @BindView(R.id.media_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.dir_layout)
    DirLayout mDirLayout;
    @BindView(R.id.tv_curDirName)
    TextView tvCurDir;
    @BindView(R.id.layout_default)
    View vDefault;
    @BindView(R.id.layout_options)
    View vOptions;

    private ArrayMap<String, List<String>> mGroupMap; //包含视频的文件夹
    public ArrayMap<String, String> sizeMap;//k-视频路径，v-视频大小（M）
    public ArrayMap<String, Integer> durationMap;//key-视频路径，value-视频时长（秒）
    private List<String> allVideoPathList;//所有视频的路径
    private List<MediaBean> mDirList;//包含视频的文件夹
    private Handler mHandler;

    public List<String> hidePathList;//隐藏的视频路径
    public String hideStrArr;//隐藏的视频路径

    public VideoAdapter mAdapter;

    private static ActAudios instance;

    public static ActAudios getInstance() {
        return instance;
    }

    public void notifyMenu() {
        if (vDefault.getVisibility() != View.GONE) {
            vDefault.setVisibility(View.GONE);
            vOptions.setVisibility(View.VISIBLE);
        } else {
            vDefault.setVisibility(View.VISIBLE);
            vOptions.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCreate() {
        setContentView(R.layout.act_select_media);
        ButterKnife.bind(this);

        instance = this;
        initVariables();
        initListeners();

        initHideList();
    }

    private void initHideList() {
        hideStrArr = SPHelper.get().getString(MConstant.HIDE_LIST);
        LogUtil.e("hideStrArr:" + hideStrArr);
        if (!hideStrArr.isEmpty()) {
            String[] hidePathArr = hideStrArr.split("\\|");
            for (String path : hidePathArr) {
                LogUtil.e("hide path:" + path);
            }
            hidePathList.addAll(Arrays.asList(hidePathArr));
        }
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
        durationMap = new ArrayMap<>();
        allVideoPathList = new ArrayList<>();
        hidePathList = new ArrayList<>();
        mHandler = new Handler();
        mAdapter = new VideoAdapter(_this, allVideoPathList, position ->
                NEXT(new Intent(_this, ActVideoPlayer.class)
                        .putExtra("index", position)));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners() {
        findViewById(R.id.ib_edit).setOnClickListener(v -> {
            notifyMenu();
            mAdapter.notifyEdit();
        });
        findViewById(R.id.btn_selectDir).setOnClickListener(view -> mDirLayout.notifyVisible());
        findViewById(R.id.btn_hide).setOnClickListener(view -> {
            if (mAdapter.getSelectedPathList().size() == 0) {
                showToast("no file selected");
                return;
            }
            String addStr = "";
            for (String path : mAdapter.getSelectedPathList()) {
                addStr += "|" + path;
                allVideoPathList.remove(path);
                mAdapter.removeItem(path);
            }
            LogUtil.e("addStr=" + addStr);
            if (hideStrArr.isEmpty()) {//之前没有存过
                addStr = addStr.replaceFirst("\\|", "");
            }
            hideStrArr += addStr;
            LogUtil.e("new hideStrArr:" + hideStrArr);
            SPHelper.get().putString(MConstant.HIDE_LIST, hideStrArr);
            mAdapter.getSelectedPathList().clear();
            mAdapter.notifyDataSetChanged();
        });
    }


    private void initVideoList() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast(getResources().getString(R.string.sdcard_nosize));
            return;
        }
        new Thread(() -> {
            scanData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            scanData(MediaStore.Video.Media.INTERNAL_CONTENT_URI);
            mGroupMap.put("所有视频", allVideoPathList);
            mHandler.post(() -> { //扫描视频完成
                mAdapter.setVideoPathList(allVideoPathList);
                mDirList = MGlobal.get().subMediaGroup(mGroupMap, true);
                initDirList();
            });
        }).start();
    }

    private void scanData(Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED);
        while (mCursor.moveToNext()) {
            // 获取视频路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
            if (!hidePathList.contains(path)) {
                allVideoPathList.add(path);
                // 获取视频时长
                long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                int s = (int) (duration / 1000); //秒
                durationMap.put(path, s);
                //视频大小(KB)
                long size = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                sizeMap.put(path, MTool.getSize(size));
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
            } else {
                LogUtil.e("跳过hide路径:" + path);
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


    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
