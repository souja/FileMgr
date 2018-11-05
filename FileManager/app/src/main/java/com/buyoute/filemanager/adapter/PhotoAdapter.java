package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.act.ActImages;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.CommonItemClickListener;
import com.buyoute.filemanager.base.GlideApp;
import com.buyoute.filemanager.base.MBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PhotoAdapter extends MBaseAdapter<String> {

    private List<String> selectedPathList;
    private boolean bEditMode = false;

    private CommonItemClickListener mListener;

    public PhotoAdapter(Context context, List<String> list, CommonItemClickListener listener) {
        super(context, list);
        mListener = listener;
        selectedPathList = new ArrayList<>();
    }

    public void notifyEdit() {
        bEditMode = !bEditMode;
        if (!bEditMode) {
            selectedPathList.clear();
        }
        notifyDataSetChanged();
    }

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

    public List<String> getSelectedPathList() {
        return selectedPathList;
    }

    public void removeSelected() {
        for (String path : selectedPathList) {
            if (mList.contains(path))
                mList.remove(path);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderPhoto(mInflater.inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderPhoto mHolder = (HolderPhoto) holder;
        if (position == 0) {
            mHolder.layoutMask.setVisibility(View.GONE);
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
                if (selectedPathList.contains(path)) {
                    selectedPathList.remove(path);
                } else {
                    selectedPathList.add(path);
                }
                notifyItemChanged(position);
            });

            mHolder.layoutMask.setOnLongClickListener(v -> {
                ActImages.getInstance().notifyMenu();
                notifyEdit();
                return false;
            });

            mHolder.ivPhoto.setOnLongClickListener(v -> {
                ActImages.getInstance().notifyMenu();
                notifyEdit();
                return false;
            });
        }
        mHolder.ivPhoto.setOnClickListener(v -> mListener.onItemClick(position));
    }

    static class HolderPhoto extends BaseHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.layout_mask)
        FrameLayout layoutMask;
        @BindView(R.id.iv_choose)
        ImageView ivChoose;

        HolderPhoto(View itemView) {
            super(itemView);
        }
    }
}
