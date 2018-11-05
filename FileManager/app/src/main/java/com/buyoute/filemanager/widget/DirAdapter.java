package com.buyoute.filemanager.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.MBaseAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Ydz on 2017/3/16 0016.
 */

public class DirAdapter extends MBaseAdapter<MediaBean> {

    private String flag;
    private int dirIndex;
    private DirClickListener mListener;

    private RequestManager mRequestManager;

    public DirAdapter(Context context, List<MediaBean> list, boolean isVideo, DirClickListener listener) {
        super(context, list);
        mRequestManager = Glide.with(context);
        flag = isVideo ? "个视频" : "张图片";
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new DirHolder(LayoutInflater.from(mContext).inflate(R.layout.item_gallery_dir, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        DirHolder mHolder = (DirHolder) holder;
        mHolder.name.setText(String.valueOf(mList.get(position).getFolderName() + ""));
        mHolder.num.setText(String.valueOf(mList.get(position).getMediaCount() + flag));
        if (dirIndex == position)
            mHolder.choice.setVisibility(View.VISIBLE);
        else
            mHolder.choice.setVisibility(View.GONE);

        mRequestManager
                .load(mList.get(position).getTopMediaPath())
                .into(mHolder.icon);

        mHolder.layout.setOnClickListener(v -> {
            dirIndex = position;
            mListener.onClick(mList.get(position));
            notifyDataSetChanged();
        });
    }

    public String getCurFolderName() {
        return mList.get(dirIndex).getFolderName();
    }

    static class DirHolder extends BaseHolder {
        @BindView(R.id.dir_icon)
        ImageView icon;
        @BindView(R.id.dir_choice)
        ImageView choice;
        @BindView(R.id.dir_name)
        TextView name;
        @BindView(R.id.dir_num)
        TextView num;
        @BindView(R.id.dir_layout)
        LinearLayout layout;

        public DirHolder(View itemView) {
            super(itemView);
        }
    }

    public interface DirClickListener {
        void onClick(MediaBean mediaBean);
    }

}