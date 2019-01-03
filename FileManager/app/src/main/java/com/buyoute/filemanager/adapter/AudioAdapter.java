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
import com.buyoute.filemanager.act.ActAudios;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.CommonItemClickListener;
import com.buyoute.filemanager.base.MBaseAdapter;
import com.buyoute.filemanager.model.OMusic;
import com.buyoute.filemanager.tool.MTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 视频列表适配器
 * Created by Ydz on 2017/3/16 0016.
 */

public class AudioAdapter extends MBaseAdapter<OMusic> {

    private CommonItemClickListener mListener;
    private boolean bEditMode = false;
    private List<String> selectedPathList;

    public AudioAdapter(Context context, List<OMusic> list, CommonItemClickListener listener) {
        super(context, list);
        mListener = listener;
        selectedPathList = new ArrayList<>();
    }

    public void setVideoPathList(List<OMusic> pathList) {
        mList = pathList;
        notifyDataSetChanged();
    }

    public List<OMusic> getPathList() {
        return mList;
    }

    public void notifyEdit() {
        bEditMode = !bEditMode;
        if (!bEditMode) {
            selectedPathList.clear();
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedPathList() {
        return selectedPathList;
    }

    public void removeItem(String path) {
        mList.remove(path);
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_audio, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        VideoHolder mHolder = (VideoHolder) holder;
        OMusic model= getItem(position);
        mHolder.tvName.setText(model.getTitle());
        mHolder.tvArtist.setText(model.getArtist());
        mHolder.tvAlbum.setText(model.getAlbum());
        mHolder.tvLength.setText(MTool.getVideoLength((int) model.getDuration()));
        mHolder.tvSize.setText(MTool.getSize(model.getSize()));

        if (bEditMode) {
            mHolder.vMask.setVisibility(View.VISIBLE);
            if (selectedPathList.contains(model.getPath())) {
                mHolder.ivChoose.setImageResource(R.drawable.ic_checked);
            } else {
                mHolder.ivChoose.setImageResource(R.drawable.ic_uncheck);
            }
        } else {
            mHolder.vMask.setVisibility(View.GONE);
            mHolder.ivChoose.setImageResource(R.drawable.ic_uncheck);
        }

        mHolder.vMask.setOnClickListener(v -> {
            if (selectedPathList.contains(model.getPath())) {
                selectedPathList.remove(model.getPath());
            } else {
                selectedPathList.add(model.getPath());
            }
            notifyItemChanged(position);
        });

        mHolder.vMask.setOnLongClickListener(v -> {
            ActAudios.getInstance().notifyMenu();
            notifyEdit();
            return false;
        });

        mHolder.layoutPreview.setOnClickListener(v -> mListener.onItemClick(position));

        mHolder.layoutPreview.setOnLongClickListener(v -> {
            ActAudios.getInstance().notifyMenu();
            notifyEdit();
            return false;
        });

    }

    static class VideoHolder extends BaseHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_artist)
        TextView tvArtist;
        @BindView(R.id.tv_album)
        TextView tvAlbum;
        @BindView(R.id.tv_length)
        TextView tvLength;
        @BindView(R.id.tv_size)
        TextView tvSize;
        @BindView(R.id.iv_choose)
        ImageView ivChoose;
        @BindView(R.id.layout_mask)
        View vMask;
        @BindView(R.id.layout_preview)
        View layoutPreview;

        VideoHolder(View itemView) {
            super(itemView);
        }
    }
}