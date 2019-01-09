package com.buyoute.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.BaseHolder;
import com.buyoute.filemanager.base.MBaseAdapter;
import com.buyoute.filemanager.tools.LogUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;

public class AdapterEmptyDir extends MBaseAdapter<String> {
    public AdapterEmptyDir(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderEmptyDir(LayoutInflater.from(mContext).inflate(R.layout.item_empty_dir, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderEmptyDir mHolder = (HolderEmptyDir) holder;
        String path = getItem(position);
        mHolder.tvPath.setText(path);
        mHolder.btnDelete.setOnClickListener(v -> {
            try {
                new File(path).delete();
                mList.remove(path);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("删除失败:" + e.getMessage());
            }
        });
    }

    static class HolderEmptyDir extends BaseHolder {
        @BindView(R.id.tv_path)
        TextView tvPath;
        @BindView(R.id.btn_delete)
        Button btnDelete;

        public HolderEmptyDir(View itemView) {
            super(itemView);
        }
    }
}
