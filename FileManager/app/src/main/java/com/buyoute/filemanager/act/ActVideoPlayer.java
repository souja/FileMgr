package com.buyoute.filemanager.act;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.adapter.VideoAdapterPlay;
import com.buyoute.filemanager.base.ActBase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;
import cn.jzvd.custom.MyJzvdStd;

public class ActVideoPlayer extends ActBase {

    @BindView(R.id.video_player)
    MyJzvdStd mJzvdStd;
    @BindView(R.id.rv_playList)
    RecyclerView mRecyclerView;

    private List<String> videoPathList;

    @Override
    public void onCreate() {
        setContentView(R.layout.act_video_player);
        ButterKnife.bind(this);
        Intent it = getIntent();

        int index = it.getIntExtra("index", 0);
        videoPathList = ActVideos.getInstance().mAdapter.getPathList();

        play(getVideoPath(index));

        VideoAdapterPlay mAdapter = new VideoAdapterPlay(_this, videoPathList, position -> {
            play(getVideoPath(position));
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void play(String path) {
        JZMediaManager.setDataSource(new JZDataSource(path, ""));
        JZMediaManager.instance().prepare();
        mJzvdStd.setUp(path, "", JzvdStd.SCREEN_WINDOW_NORMAL);
        mJzvdStd.startVideo();
    }

    private String getVideoPath(int index) {
        return videoPathList.get(index);
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }


    private int pauseState;

    @Override
    protected void onPause() {
        super.onPause();
        pauseState = mJzvdStd.currentState;
        if (mJzvdStd.currentState == Jzvd.CURRENT_STATE_PLAYING) {
            mJzvdStd.onEvent(JZUserAction.ON_CLICK_PAUSE);
            JZMediaManager.pause();
            mJzvdStd.onStatePause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (pauseState == Jzvd.CURRENT_STATE_PLAYING) {
            mJzvdStd.onEvent(JZUserAction.ON_CLICK_RESUME);
            JZMediaManager.start();
            mJzvdStd.onStatePlaying();
        }
    }


    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        try {
            if (JZMediaManager.isPlaying())
                JZMediaManager.pause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        JZMediaManager.instance().releaseMediaPlayer();
        JzvdMgr.completeAll();
        super.onBackPressed();
    }
}
