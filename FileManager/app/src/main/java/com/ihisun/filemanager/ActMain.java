package com.ihisun.filemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.ihisun.filemanager.adapter.AdapterCategories;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActMain extends AppCompatActivity {
    @BindView(R.id.rv_cats)
    RecyclerView rvCats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);

        rvCats.setAdapter(new AdapterCategories(this, str -> {
            switch (str) {
                case "图片":
                    break;
            }
        }));
    }
}
