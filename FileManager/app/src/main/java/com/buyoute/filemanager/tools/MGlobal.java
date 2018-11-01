package com.buyoute.filemanager.tools;

import android.graphics.PointF;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.buyoute.filemanager.widget.MediaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Created by Souja on 2018/3/12 0012.
 */

public class MGlobal {
    private static MGlobal instance;

    public static MGlobal get() {
        if (instance == null) {
            synchronized (MGlobal.class) {
                if (instance == null) {
                    instance = new MGlobal();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private void init() {
        actionMap = new ArrayMap<>();
    }

    public static boolean bQmp;//是否全面屏（刘海屏）

    public void initScreenParam(DisplayMetrics dm) {
        this.deviceWidth = dm.widthPixels;
        this.deviceHeight = dm.heightPixels;
        this.density = dm.density;

        LogUtil.e("[DeviceInfo]dpi=" + dm.densityDpi + ",width=" + deviceWidth
                + ",height=" + dm.heightPixels + ",density=" + dm.density
                + ",scaledDensity=" + dm.scaledDensity
                + ",xdpi=" + dm.xdpi + ",ydpi=" + dm.ydpi);
        LogUtil.e("mScale=" + (double) deviceWidth / 1080d);

        int sw = MGlobal.get().getDeviceWidth(), sh = MGlobal.get().getDeviceHeight();
        bQmp = (sw == 1080 && sh > 1920) || (sw == 1440 && sh > 2560);
        LogUtil.e(bQmp ? "全面/刘海屏手机" : "非 全面/刘海屏手机");
    }

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public int getDeviceHeight() {
        return deviceHeight;
    }

    public float getDensity() {
        return density;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public int getDpi() {
        return dpi;
    }

    private int deviceWidth, deviceHeight;
    private float density;
    private ArrayMap<Integer, Consumer<Object>> actionMap;
    private int dpi;

    //Rx functions======>>>
    public void addAction(int key, Consumer<Object> consumer) {
        actionMap.put(key, consumer);
    }

    public Consumer<Object> getAction(int key) {
        if (actionMap.containsKey(key))
            return actionMap.get(key);
        else return null;
    }

    public void delAction(int key) {
        if (actionMap.containsKey(key))
            actionMap.remove(key);
    }

    public boolean containsKey(int key) {
        return actionMap.containsKey(key);
    }

    public void clearActions() {
        if (actionMap == null) return;
        actionMap.clear();
    }


    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     */
    public List<MediaBean> subMediaGroup(ArrayMap<String, List<String>> groupMap, boolean isVideo) {
        if (groupMap.size() == 0) {
            return null;
        }
        List<MediaBean> list = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> it = groupMap.entrySet().iterator();
        String flag = isVideo ? "所有视频" : "所有图片";
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            MediaBean mMediaBean = new MediaBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value.size() > 0) {
                mMediaBean.setFolderName(key);
                mMediaBean.setMediaCount(value.size());
                mMediaBean.setTopMediaPath(value.get(0));//获取该组的第1个media的路径
                if (key.equals(flag))
                    list.add(0, mMediaBean);
                else
                    list.add(mMediaBean);
            }
        }
        return list;

    }

    public static double getDistance(PointF p1, PointF p2) {
        double _x = Math.abs(p1.x - p2.x);
        double _y = Math.abs(p1.y - p2.y);
        return Math.sqrt(_x * _x + _y * _y);
    }

    /**
     * 获取手指间的距离
     *
     * @param event
     * @return
     */
    public static float getSpaceDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 获取手势中心点
     *
     * @param event
     */
    public static PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    private int keybordHeight;

    public void setKeybordHeight(int height) {
        keybordHeight = height;
    }

    public int getKeybordHeight() {
        return keybordHeight;
    }
}
