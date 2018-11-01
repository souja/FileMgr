package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.MBaseAdapter;

import java.util.List;

import butterknife.BindView;

public class AdapterMenu extends MBaseAdapter<String> {


    public AdapterMenu(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderMenu(mInflater.inflate(R.layout.item_menu, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderMenu mHolder = (HolderMenu) holder;
        mHolder.tvMenu.setText(getItem(position));
    }

    static class HolderMenu extends BaseHolder {
        @BindView(R.id.tv_menu)
        TextView tvMenu;

        public HolderMenu(View itemView) {
            super(itemView);
        }
    }
}
