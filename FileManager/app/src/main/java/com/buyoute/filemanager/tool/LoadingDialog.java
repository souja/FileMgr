package com.buyoute.filemanager.tool;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.buyoute.filemanager.R;

/**
 * 菊花...
 * Created by Yangdz on 2015/2/4.
 */
public class LoadingDialog extends ProgressDialog {

    private Context mContext;
    private TextView tvMsg;
    private String msg;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
    }

    public LoadingDialog(Context context, String msg) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
        this.msg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.progress_dialog, null);
        tvMsg = contentView.findViewById(R.id.content);
        if (msg != null) tvMsg.setText(msg);
        setCanceledOnTouchOutside(false);
        setContentView(contentView);


//        设置透明度
//        Window window = getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 1f;// 透明度
//        lp.dimAmount = 0f;// 黑暗度
//        window.setAttributes(lp);
//        int start = flipper.getDisplayedChild() + 1;
//        if (totalPage > 0) {
//            final int total = totalPage;
//            dialog.setIndeterminate(false);
//            dialog.incrementProgressBy(start * totalPage / total);
//            dialog
//                    .setX(this.getWindowManager().getDefaultDisplay()
//                            .getWidth() - 40);
    }

    public void setMsg(String msg) {
        tvMsg.setText(msg);
    }
}