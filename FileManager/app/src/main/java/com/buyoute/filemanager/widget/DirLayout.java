package com.buyoute.filemanager.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.buyoute.filemanager.R;

/**
 * Created by Ydz on 2017/3/16 0016.
 */

public class DirLayout extends RelativeLayout {

    public DirLayout(Context context) {
        super(context);
        init(context);
    }

    public DirLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DirLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private RecyclerView mRecyclerView;
    private Animation show, hide;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_dir_layout, this);
        mRecyclerView = findViewById(R.id.dir_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        show = AnimationUtils.loadAnimation(context, R.anim.dialog_bottom_in);
        hide = AnimationUtils.loadAnimation(context, R.anim.dialog_bottom_out);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        setOnClickListener(view -> dismiss());
    }

    public void setAdapter(DirAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    public void show() {
        setVisibility(VISIBLE);
        startAnimation(show);
    }

    public void dismiss() {
        startAnimation(hide);
    }

    public void setListLayoutParams(LayoutParams params) {
        mRecyclerView.setLayoutParams(params);
    }

    public void notifyVisible() {
        if (!isShowing()) {
            show();
        } else {
            dismiss();
        }
    }
}
