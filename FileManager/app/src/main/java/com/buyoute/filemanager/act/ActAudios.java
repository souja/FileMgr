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
import com.buyoute.filemanager.adapter.AudioAdapter;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.model.OMusic;
import com.buyoute.filemanager.tool.MGlobal;
import com.buyoute.filemanager.widget.DirAdapter;
import com.buyoute.filemanager.widget.DirLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 选择视频
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

    private ArrayMap<String, List<OMusic>> mGroupMap; //包含视频的文件夹
    public ArrayMap<String, String> sizeMap;//k-视频路径，v-视频大小（M）
    public ArrayMap<String, Integer> durationMap;//key-视频路径，value-视频时长（秒）
    private List<OMusic> allVideoPathList;//所有视频的路径
    private Handler mHandler;

    public AudioAdapter mAdapter;

    private static ActAudios instance;

    @Override
    protected int setupViewRes() {
        return R.layout.act_audios;
    }

    @Override
    protected void initMain() {
        ButterKnife.bind(this);

        instance = this;
        initVariables();
        initAudioList();
        initListeners();
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

    private void initVariables() {
        mGroupMap = new ArrayMap<>();
        sizeMap = new ArrayMap<>();
        durationMap = new ArrayMap<>();
        allVideoPathList = new ArrayList<>();
        mHandler = new Handler();
        mAdapter = new AudioAdapter(_this, allVideoPathList, position ->
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
    }

    private void initAudioList() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast(getResources().getString(R.string.sdcard_nosize));
            return;
        }
        new Thread(() -> {
            scanData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            scanData(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
            mGroupMap.put("所有音频", allVideoPathList);
            mHandler.post(() -> { //扫描视频完成
                mAdapter.setVideoPathList(allVideoPathList);
                mDirLayout.setAdapter(new DirAdapter(_this,
                        MGlobal.get().subAudioGroup(mGroupMap, true),
                        true, mediaBean -> {
                    String key = mediaBean.getFolderName();
                    tvCurDir.setText(key);
                    mAdapter.setVideoPathList(mGroupMap.get(key));
                    mDirLayout.dismiss();
                }));
            });
        }).start();
    }

    private void scanData(Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };
//         String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 " ;

        Cursor mCursor = mContentResolver.query(uri,
                projection,
                null,
                null,
                MediaStore.Audio.Media.DATE_MODIFIED);


        int displayNameCol = mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
        int albumCol = mCursor.getColumnIndex(MediaStore.Video.Media.ALBUM);
        int idCol = mCursor.getColumnIndex(MediaStore.Video.Media._ID);
        int durationCol = mCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
        int sizeCol = mCursor.getColumnIndex(MediaStore.Video.Media.SIZE);
        int artistCol = mCursor.getColumnIndex(MediaStore.Video.Media.ARTIST);
        int pathCol = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);

        while (mCursor.moveToNext()) {
            //标题
            String title = mCursor.getString(displayNameCol);
            //专辑
            String album = mCursor.getString(albumCol);
            long id = mCursor.getLong(idCol);
            //Singer
            String artist = mCursor.getString(artistCol);
            // 路径
            String path = mCursor.getString(pathCol);
            //时长
            long duration = mCursor.getLong(durationCol);
            int s = (int) (duration / 1000); //秒
            durationMap.put(path, s);
            //大小(KB)
            long size = mCursor.getLong(sizeCol);
            // 获取该视频的父路径名
            String dirName = new File(path).getParentFile().getName();

            OMusic music = new OMusic(id, title, album, duration, size, artist, path);

            allVideoPathList.add(music);

            // 根据父路径名将视频放入到mGruopMap中
            if (!mGroupMap.containsKey(dirName)) {
                List<OMusic> childList = new ArrayList<>();
                childList.add(music);
                mGroupMap.put(dirName, childList);
            } else {
                mGroupMap.get(dirName).add(music);
            }
        }
        mCursor.close();
    }

    public static ActAudios getInstance() {
        return instance;
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
