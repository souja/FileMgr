package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.act.ActVideos;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.CommonItemClickListener;
import com.buyoute.filemanager.base.GlideApp;
import com.buyoute.filemanager.base.MBaseAdapter;
import com.buyoute.filemanager.tools.MTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 视频列表适配器
 * Created by Ydz on 2017/3/16 0016.
 */

public class VideoAdapter extends MBaseAdapter<String> {

    private CommonItemClickListener mListener;
    private boolean bEditMode = false;
    private List<String> selectedPathList;

    public VideoAdapter(Context context, List<String> list, CommonItemClickListener listener) {
        super(context, list);
        mListener = listener;
        selectedPathList = new ArrayList<>();
    }

    public void setVideoPathList(List<String> pathList) {
        mList = pathList;
        notifyDataSetChanged();
    }

    public List<String> getPathList() {
        return mList;
    }

    private void notifyEdit() {
        bEditMode = !bEditMode;
        if (!bEditMode) {
            selectedPathList.clear();
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedPathList() {
        return selectedPathList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        VideoHolder mHolder = (VideoHolder) holder;
        String path = mList.get(position);

        GlideApp.with(mContext)
                .load(path)
                .placeholder(R.drawable.ic_loading_blue)
                .into(mHolder.mImageView);

        if (ActVideos.getInstance().durationMap.containsKey(path)) {
            mHolder.tvLength.setText(MTool.getVideoLength(ActVideos.getInstance().durationMap.get(path)));
        } else {
            mHolder.tvLength.setText(String.valueOf("??:??"));
        }
        if (ActVideos.getInstance().sizeMap.containsKey(path)) {
            mHolder.tvSize.setText(ActVideos.getInstance().sizeMap.get(path));
        } else {
            mHolder.tvSize.setText(String.valueOf("??KB"));
        }

        if (bEditMode) {
            mHolder.layoutMask.setVisibility(View.VISIBLE);
            if (selectedPathList.contains(path)) {
                mHolder.ivChoose.setImageResource(R.drawable.ic_checked);
            } else {
                mHolder.ivChoose.setImageResource(R.drawable.ic_uncheck);
            }
        } else {
            mHolder.layoutMask.setVisibility(View.GONE);
            mHolder.ivChoose.setImageResource(R.drawable.ic_uncheck);
        }

        mHolder.layoutMask.setOnClickListener(v -> {
            ActVideos.getInstance().notifyMenu();
            if (selectedPathList.contains(path)) {
                selectedPathList.remove(path);
            } else {
                selectedPathList.add(path);
            }
            notifyItemChanged(position);
        });

        mHolder.layoutMask.setOnLongClickListener(v -> {
            notifyEdit();
            ActVideos.getInstance().notifyMenu();
            return false;
        });
        mHolder.itemView.setOnClickListener(v -> mListener.onItemClick(position));

        mHolder.itemView.setOnLongClickListener(v -> {
            notifyEdit();
            return false;
        });
    }

    static class VideoHolder extends BaseHolder {
        @BindView(R.id.iv_preview)
        ImageView mImageView;
        @BindView(R.id.tv_length)
        TextView tvLength;
        @BindView(R.id.tv_size)
        TextView tvSize;
        @BindView(R.id.iv_choose)
        ImageView ivChoose;
        @BindView(R.id.layout_mask)
        FrameLayout layoutMask;

        VideoHolder(View itemView) {
            super(itemView);
        }
    }
}