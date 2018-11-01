package com.buyoute.filemanager.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Ydz on 2017/7/5 0005.
 */
public abstract class MBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context mContext;
    protected List<T> mList;
    protected LayoutInflater mInflater;

    public MBaseAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    public abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateView(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindView(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public boolean isLastItem(int position) {
        return position == mList.size() - 1;
    }

    public T getItem(int position) {
        return mList.get(position);
    }
}

