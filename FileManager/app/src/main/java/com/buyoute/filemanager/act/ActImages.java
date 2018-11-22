package com.buyoute.filemanager.act;

import android.content.ActivityNotFoundException;
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
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.buyoute.filemanager.EncriptDemo;
import com.buyoute.filemanager.FMApp;
import com.buyoute.filemanager.R;
import com.buyoute.filemanager.adapter.PhotoAdapter;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.model.OEncryptMedia;
import com.buyoute.filemanager.tool.DialogFactory;
import com.buyoute.filemanager.tool.FilePath;
import com.buyoute.filemanager.tool.FileUtil;
import com.buyoute.filemanager.tool.LoadingDialog;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;

import com.buyoute.filemanager.tool.MDateUtils;
import com.buyoute.filemanager.tool.MGlobal;
import com.buyoute.filemanager.widget.DirAdapter;
import com.buyoute.filemanager.widget.DirLayout;

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
    @BindView(R.id.layout_default)
    View vDefault;
    @BindView(R.id.layout_options)
    View vOptions;

    public static final int REQUEST_CODE2 = 10101;

    public ArrayMap<String, List<String>> mPathMap; //key-文件夹名称，value-图片路径List

    public List<String> mListPathAllImg;   //所有图片的路径
    public PhotoAdapter mPhotoAdapter;

    private static ActImages instance;

    private Handler handler = new Handler();

    private File cameraFile;

    private LoadingDialog mLoadingDialog;

    @Override
    protected int setupViewRes() {
        return R.layout.act_imgs;
    }

    @Override
    protected void initMain() {
        ButterKnife.bind(this);
        instance = this;

        initListeners();
        mLoadingDialog = new LoadingDialog(_this);
        mPathMap = new ArrayMap<>();
        mListPathAllImg = new ArrayList<>();
        mPhotoAdapter = new PhotoAdapter(this, mListPathAllImg, position -> {
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
        mediaList.setAdapter(mPhotoAdapter);
        getImages();
    }

    private void initListeners() {
        btnSelectDir.setOnClickListener(view -> dirLayout.notifyVisible());
        findViewById(R.id.ib_edit).setOnClickListener(v -> {
            notifyMenu();
            mPhotoAdapter.notifyEdit();
        });
        findViewById(R.id.btn_delete).setOnClickListener(view -> {
            if (mPhotoAdapter.getSelectedPathList().size() == 0) return;
            DialogFactory.NewDialog(_this, null, "确定删除？",
                    "确定", (d, i) -> {
                        d.dismiss();
                        mLoadingDialog.show();
                        for (String path : mPhotoAdapter.getSelectedPathList()) {
                            mListPathAllImg.remove(path);
                            FileUtils.deleteFile(path);
                        }
                        mPhotoAdapter.removeSelected();
                        mLoadingDialog.dismiss();
                    }, "取消", (d, i) -> d.dismiss()).show();
        });
        findViewById(R.id.btn_encrypt).setOnClickListener(view -> {
            if (mPhotoAdapter.getSelectedPathList().size() == 0) return;
            DialogFactory.NewDialog(_this, null, "确定加密？",
                    "确定", (d, i) -> {
                        d.dismiss();
                        mLoadingDialog.show();
                        for (String path : mPhotoAdapter.getSelectedPathList()) {
                            File oriFile = new File(path);
                            String enFileName = oriFile.getName();
                            LogUtil.e(oriFile.getAbsolutePath());

                            File destFile = new File(FilePath.getEncryptPath(),
                                    MDateUtils.getCurrentDate2() + enFileName + ".dat");
                            try {
                                OEncryptMedia mOEncryptMedia = new OEncryptMedia(destFile);
                                FMApp.getDBManager().saveOrUpdate(mOEncryptMedia);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                            try {
                                EncriptDemo.encrImg(oriFile, destFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mListPathAllImg.remove(path);
                            mPhotoAdapter.removeSelected();
                        }
                        mLoadingDialog.dismiss();
                    }, "取消", (d, i) -> d.dismiss()).show();
        });
    }

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
            mPathMap.put("所有图片", mListPathAllImg);
            handler.post(() -> {
                mPhotoAdapter.setPhotoPathList(mListPathAllImg);
                dirLayout.setAdapter(new DirAdapter(this,
                        MGlobal.get().subMediaGroup(mPathMap, false),
                        false, mediaBean -> {
                    String key = mediaBean.getFolderName();
                    tvCurDir.setText(key);
                    mPhotoAdapter.setPhotoPathList(mPathMap.get(key));
                    dirLayout.dismiss();
                }));
            });
        }).start();
    }

    private void scanData(Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            mListPathAllImg.add(path);
            // 获取该图片的父路径名
            String parentName = new File(path).getParentFile().getName();
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

        String curFolder = dirLayout.getCurFolderName();
        LogUtil.e("curFolder : " + curFolder);
        mPathMap.get(curFolder).add(editPath);//添加到数据源
        //添加到当前图片目录
        mPhotoAdapter.addPath(editPath);
    }

    private void AddPicToScan(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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

    public static ActImages getInstance() {
        return instance;
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
