package com.buyoute.filemanager.act;

import android.os.Environment;
import android.support.v7.widget.RecyclerView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.adapter.AdapterEmptyDir;
import com.buyoute.filemanager.base.ActBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActEmptyDIrs extends ActBase {
    @BindView(R.id.rv_dirs)
    RecyclerView mRecyclerView;

    private AdapterEmptyDir mAdapter;
    private List<String> mList;

    @Override
    public void onCreate() {
        setContentView(R.layout.act_empty_dirs);
        ButterKnife.bind(this);

        mList = new ArrayList<>();
        mAdapter = new AdapterEmptyDir(_this, mList);
        mRecyclerView.setAdapter(mAdapter);

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        new Thread(()->addItem(root)).start();

        findViewById(R.id.btn_delete).setOnClickListener(v -> {
            for (String s : mList) {
                new File(s).delete();
            }
        });
    }

    private void addItem(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.length() == 0) {
                    mList.add(f.getAbsolutePath());
                } else {
                    if (f.isDirectory()) {
                        addItem(f);
                    }
                }
            }
        }
    }

    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }
}
