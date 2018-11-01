package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.CommonItemClickListener;
import com.buyoute.filemanager.base.GlideApp;
import com.buyoute.filemanager.base.MBaseAdapter;

import java.util.List;

import butterknife.BindView;

public class PhotoAdapter extends MBaseAdapter<String> {

    private CommonItemClickListener mListener;

    public void setPhotoPathList(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addPath(String path) {
        mList.add(0, path);
        notifyDataSetChanged();
    }

    public List<String> getPathList() {
        return mList;
    }

    public PhotoAdapter(Context context, List<String> list, CommonItemClickListener listener) {
        super(context, list);
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderPhoto(mInflater.inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderPhoto mHolder = (HolderPhoto) holder;
        if (position == 0) {
            GlideApp.with(mContext)
                    .load(R.drawable.ic_camera)
                    .fitCenter()
                    .into(mHolder.ivPhoto);
        } else {
            String path = mList.get(position - 1);
            GlideApp.with(mContext)
                    .load(path)
                    .fitCenter()
                    .placeholder(R.drawable.ic_loading_blue)
                    .into(mHolder.ivPhoto);
        }
        mHolder.itemView.setOnClickListener(v -> mListener.onItemClick(position));
    }

    static class HolderPhoto extends BaseHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        HolderPhoto(View itemView) {
            super(itemView);
        }
    }
}
