package com.buyoute.filemanager.act;

import android.content.ActivityNotFoundException;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.adapter.PhotoAdapter;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.tools.FileUtil;
import com.buyoute.filemanager.tools.LogUtil;
import com.buyoute.filemanager.tools.MGlobal;
import com.buyoute.filemanager.widget.DirAdapter;
import com.buyoute.filemanager.widget.DirLayout;
import com.buyoute.filemanager.widget.MediaBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActImages extends ActBase {

    @BindView(R.id.btn_selectDir)
    Button btnSelectDir;
    @BindView(R.id.media_list)
    RecyclerView mediaList;
    @BindView(R.id.dir_layout)
    DirLayout dirLayout;
    @BindView(R.id.tv_curDirName)
    TextView tvCurDir;

    public static final int REQUEST_CODE1 = 10100;
    public static final int REQUEST_CODE2 = 10101;

    public ArrayMap<String, List<String>> mPathMap; //key-文件夹名称，value-图片路径List

    public List<String> allImageList;   //所有图片的路径
    public PhotoAdapter mAdapter;

    private List<MediaBean> mDirList;   //相册目录List
    private DirAdapter mDirAdapter;

    private static ActImages instance;

    public static ActImages getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_imgs);
        ButterKnife.bind(this);
        instance = this;
        init();
    }

    private File cameraFile;

    private void init() {
        btnSelectDir.setOnClickListener(view -> dirLayout.notifyVisible());

        mPathMap = new ArrayMap<>();
        allImageList = new ArrayList<>();
        mAdapter = new PhotoAdapter(this, allImageList, position -> {
            if (position == 0) {//拍照
                try {
                    cameraFile = FileUtil.setUpPhotoFile();
                    LogUtil.e("cameraFile path=" + cameraFile.getAbsolutePath()
                            + ",exist=" + cameraFile.exists());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    startActivityForResult(intent, REQUEST_CODE2);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(e.getMessage());
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    showToast("相机设备异常");
                }
            } else {
                LogUtil.e("go in gallery prepare...");
                Intent it = new Intent(this, ActImgGallery.class);
                it.putExtra(ActImgGallery.IMAGES_INDEX, position - 1);
                NEXT(it);
            }
        });
        mediaList.setAdapter(mAdapter);
        getImages();
    }

    private Handler handler = new Handler();   //处理器

    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast(R.string.sdcard_nosize);
            return;
        }

        // 显示进度条
        new Thread(() -> {
            scanData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //通知Handler扫描图片完成
            mPathMap.put("所有图片", allImageList);
            handler.post(() -> {
                mAdapter.setPhotoPathList(allImageList);
                mDirList = MGlobal.get().subMediaGroup(mPathMap, false);
                initDirList();
            });
        }).start();
    }
    private void initDirList() {
        mDirAdapter = new DirAdapter(this, mDirList, false, mediaBean -> {
            String key = mediaBean.getFolderName();
            tvCurDir.setText(key);
            mAdapter.setPhotoPathList(mPathMap.get(key));
            dirLayout.dismiss();
        });
        dirLayout.setAdapter(mDirAdapter);
    }


    private void scanData(Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            allImageList.add(path);
            // 获取该图片的父路径名
            String parentName = new File(path).getParentFile()
                    .getName();
            // 根据父路径名将图片放入到mGruopMap中
            if (!mPathMap.containsKey(parentName)) {
                List<String> childList = new ArrayList<>();
                childList.add(path);
                mPathMap.put(parentName, childList);
            } else {
                mPathMap.get(parentName).add(path);
            }
        }
        mCursor.close();
    }

    //编辑（涂鸦等）产生了新的图片
    public void onEditImg(String oriPath, String editPath) {
        //添加到图库
        AddPicToScan(editPath);

        String curFolder = mDirList.get(mDirAdapter.getDirIndex()).getFolderName();
        LogUtil.e("curFolder : " + curFolder);
        mPathMap.get(curFolder).add(editPath);//添加到数据源
        //添加到当前图片目录
        mAdapter.addPath(editPath);
    }

    private void AddPicToScan(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }
}
