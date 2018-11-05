package com.buyoute.filemanager.act;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.buyoute.filemanager.EncriptDemo;
import com.buyoute.filemanager.FMApp;
import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.model.OEncryptMedia;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActPrivate extends ActBase {

    @BindView(R.id.tbl_cat)
    TabLayout tblCat;
    @BindView(R.id.vp_cat)
    ViewPager vpCat;
    @BindView(R.id.iv_test)
    ImageView ivTest;

    private final String[] cats = {"图片", "视频"};

    @Override
    public void onCreate() {
        setContentView(R.layout.act_private);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        try {
            List<OEncryptMedia> imgList = FMApp.getDBManager().selector(OEncryptMedia.class)
                    .where("type", "=", 0).findAll();

            if (imgList != null && imgList.size() > 0) {
                LogUtil.e("Read img list cache info suc, data length is " + imgList.size());
                try {
                    File enFile = new File(imgList.get(0).getParent(),
                            imgList.get(0).getName().replace(".data", ""));
                    if (enFile.exists()) {
                        LogUtil.e("Read file:" + enFile.getAbsolutePath());
                        ivTest.setImageBitmap(EncriptDemo.readBitmap(enFile));
                    } else {
                        LogUtil.e("File not exist:" + enFile.getPath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("eeeeeee:" + e.getMessage());
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }
}
