package com.buyoute.filemanager.act;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.buyoute.filemanager.R;
import com.buyoute.filemanager.base.ActBase;
import com.buyoute.filemanager.tool.FilePath;
import org.xutils.common.util.LogUtil;
import com.buyoute.filemanager.tool.MDateUtils;
import com.buyoute.filemanager.widget.HackyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kareluo.imaging.IMGEditActivity;
import uk.co.senab.photoview.PhotoView;

public class ActImgGallery extends ActBase {

    @BindView(R.id.gallery_vp)
    HackyViewPager galleryVp;
    @BindView(R.id.btn_edit)
    AppCompatButton btnEdit;
    @BindView(R.id.tv_index)
    TextView tvIndex;

    private HackyViewPager viewPager;
    private AppCompatButton btnEditImg;

    //裁剪图片的requestCode
    public static final int REQUEST_CODE_CROP_IMAGE = 10002;
    //intent添加图片的key
    public static final String IMAGE_PATH_LIST_SELECTED = "image1";
    //intent添加图片的图片张数
    public static final String IMAGES_COUNT = "image3";
    //intent添加图片的图片下标
    public static final String IMAGES_INDEX = "image4";

    @Override
    protected int setupViewRes() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void initMain() {
        ButterKnife.bind(this);

        viewPager = findViewById(R.id.gallery_vp);
        btnEditImg = findViewById(R.id.btn_edit);
        initIntent();
        init();
    }

    private void initIntent() {
        Intent it = getIntent();
        index = it.getIntExtra(ActImgGallery.IMAGES_INDEX, 1);

        mListAll.addAll(ActImages.getInstance().mPhotoAdapter.getPathList());

        updateImgIndex();
    }

    private int REQ_IMAGE_EDIT = 2018;
    private File editFile;
    private MyPagerAdapter adapter;

    private List<String> mListAll = new ArrayList<>();
    private int index = 0;
    private String curSelectedPath;

    private void init() {
        initVp();
        //编辑
        btnEditImg.setOnClickListener(v -> {
            curSelectedPath = mListAll.get(viewPager.getCurrentItem());
            LogUtil.e("current selected path:" + curSelectedPath);
            File selectedFile = new File(curSelectedPath);
            LogUtil.e("file exist:" + selectedFile.exists());
            if (selectedFile.exists()) {
                Uri uri = Uri.fromFile(selectedFile);
                editFile = new File(FilePath.getTempPicturePath() + "/edit_" + MDateUtils.getCurrentDate2() + ".jpg");
                startActivityForResult(
                        new Intent(this, IMGEditActivity.class)
                                .putExtra("IMAGE_URI", uri)
                                .putExtra("IMAGE_SAVE_PATH", editFile.getAbsolutePath()),
                        REQ_IMAGE_EDIT);
            } else {
                showToast("图片加载失败");
            }
        });
    }

    private void updateImgIndex() {
        tvIndex.setText(String.valueOf((index + 1) + "/" + mListAll.size()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_IMAGE_EDIT) {
            if (resultCode == RESULT_OK) {
                String editPath = editFile.getAbsolutePath();

                ActImages.getInstance().onEditImg(curSelectedPath, editPath);
                mListAll.add(viewPager.getCurrentItem(), editPath);
                adapter.notifyDataSetChanged();
                updateImgIndex();
            }
        }
    }

    private void initVp() {
        adapter = new MyPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int posi) {
                index = posi;
                tvIndex.setText(String.valueOf((posi + 1) + "/" + mListAll.size()));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int posi) {
            }
        });
        updateCurItemSelectStatus();
    }

    private void updateCurItemSelectStatus() {
        viewPager.setCurrentItem(index);
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        private Context mContext;

        public MyPagerAdapter(Context context) {
            mContext = context;
            mRequestManager = Glide.with(mContext);
        }

        private RequestManager mRequestManager;

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int arg1, Object arg2) {
            container.removeView((View) arg2);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListAll.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int posi) {
            String path = mListAll.get(posi);
            PhotoView imageView = new PhotoView(_this);
            mRequestManager.load(path).into(imageView);
            container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }


    @Override
    public void onPermissionDisable() {

    }

    @Override
    public void onPermissionGranted() {

    }
}
