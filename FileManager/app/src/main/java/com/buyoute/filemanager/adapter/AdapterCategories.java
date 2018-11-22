package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.CommonItemClickListener;

import butterknife.BindView;

public class AdapterCategories extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final int[] categoryNames = {R.string.cat_img, R.string.cat_video, R.string.cat_music,
            R.string.cat_doc, R.string.cat_apk};
    private final int[] categoryIcons = {R.drawable.ic_img, R.drawable.ic_video,
            R.drawable.ic_music, R.drawable.ic_doc, R.drawable.ic_and};

    private Context mContext;
    private CommonItemClickListener mListener;

    public AdapterCategories(Context context, CommonItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderCategory(LayoutInflater.from(mContext)
                .inflate(R.layout.item_category, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HolderCategory mHolder = (HolderCategory) viewHolder;
        mHolder.tvCatName.setText(categoryNames[i]);
        mHolder.ivCatIcon.setImageResource(categoryIcons[i]);
        mHolder.itemView.setOnClickListener(v -> mListener.onItemClick(categoryNames[i]));
    }

    @Override
    public int getItemCount() {
        return categoryNames.length;
    }

    static class HolderCategory extends BaseHolder {
        @BindView(R.id.iv_catIcon)
        ImageView ivCatIcon;
        @BindView(R.id.tv_catName)
        TextView tvCatName;

        HolderCategory(View itemView) {
            super(itemView);
        }
    }
}
