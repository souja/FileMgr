package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.CommonItemClickListener;
import com.buyoute.filemanager.base.GlideApp;
import com.buyoute.filemanager.base.MBaseAdapter;
import com.buyoute.filemanager.tools.MTool;

import java.util.List;

import butterknife.BindView;

/**
 * 视频列表适配器
 * Created by Ydz on 2017/3/16 0016.
 */

public class VideoAdapter extends MBaseAdapter<String> {

    private CommonItemClickListener mListener;
    private ArrayMap<String, Integer> durationMap;

    public void setDurationMap(ArrayMap<String, Integer> map) {
        durationMap = map;
    }

    public VideoAdapter(Context context, List<String> list, CommonItemClickListener listener) {
        super(context, list);
        mListener = listener;
    }

    public void setVideoPathList(List<String> pathList) {
        mList = pathList;
        notifyDataSetChanged();
    }

    public List<String> getPathList() {
        return mList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        VideoHolder mHolder = (VideoHolder) holder;
        String path = mList.get(position);
        if (durationMap != null) {
            if (durationMap.containsKey(path)) {
                mHolder.mTextView.setText(MTool.getVideoLength(durationMap.get(path)));
            } else {
                mHolder.mTextView.setText(String.valueOf("00:00"));
            }
        }

        GlideApp.with(mContext)
                .load(path)
                .placeholder(R.drawable.ic_loading_blue)
                .into(mHolder.mImageView);

        mHolder.itemView.setOnClickListener(v -> mListener.onItemClick(position));
    }

    static class VideoHolder extends BaseHolder {
        @BindView(R.id.iv_mediaPreview)
        ImageView mImageView;
        @BindView(R.id.tv_desc)
        TextView mTextView;

        VideoHolder(View itemView) {
            super(itemView);
        }
    }
}