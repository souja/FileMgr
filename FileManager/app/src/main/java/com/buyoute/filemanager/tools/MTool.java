package com.buyoute.filemanager.tools;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Souja on 2018/6/29 0029.
 */

public class MTool {

    public static void Toast(Context context, String msg) {
        if (msg == null || msg.contains("onNext") || context == null) return;
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void Toast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void Toast(Context context, String msg, int duration) {
        if (msg == null || msg.contains("onNext") || context == null) return;
        Toast.makeText(context, msg, duration).show();
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("((^(13|14|15|17||18)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
                + "(^0[3-9] {1}d{2}-?d{7,8}$)|"
                + "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
                + "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * 全透状态栏
     */
    public static void setStatusBarFullTransparent(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 设置StatusBar字体颜色
     * <p>
     * 参数true表示StatusBar风格为Light，字体颜色为黑色
     * 参数false表示StatusBar风格不是Light，字体颜色为白色
     * <p>
     * <item name="android:windowLightStatusBar">true</item>
     * 在theme或style中使用这个属性改变StatusBar的字体颜色，这种形式相对不灵活
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void setStatusBarTextColor(Window window, boolean lightStatusBar) {
        if (window == null) return;
        View decor = window.getDecorView();
        int ui = decor.getSystemUiVisibility();
        if (lightStatusBar) {
            ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decor.setSystemUiVisibility(ui);
    }

    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    public static int getCurrentVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            LogUtil.e("当前版本信息：name=" + info.versionName + ",code=" + info.versionCode);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取当前版本信息出错");
            return 0;
        }
    }

    public static int getNavigationBarHeight(Context context) {
//        Resources resources = context.getResources();
//        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//        int height = resources.getDimensionPixelSize(resourceId);
//        return height;
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e("keybord height:" + vh);
        return vh;
    }

    public static String getVideoLength(int dur) {
        int h = dur / 3600;
        int m = (dur - h * 3600) / 60;
        int s = (dur - h * 3600) % 60;

        String hh = h < 10 ? "0" + h : "" + h;//小时

        String mm = m < 10 ? "0" + m : "" + m;//分钟

        String ss = s < 10 ? "0" + s : "" + s;//秒钟

        return hh + ":" + mm + ":" + ss;
    }
}
//    public static String getCnLevel(int index) {
//        final String[] cnLevels = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
//                "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
//        return cnLevels[index];
//    }

/*

//    public static void Dial(AppCompatActivity mContext, String phoneNo) {
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
//        mContext.startActivity(intent);
//    }
    public static void SendSMS(AppCompatActivity mContext, String phoneNoArr, String msg) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNoArr)));
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            intent.putExtra("sms_body", msg);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.toString());
        }
    }

    public static String getCnIndex(int i) {
        String cnIndex;
        switch (i) {
            case 1:
                cnIndex = "二";
                break;
            case 2:
                cnIndex = "三";
                break;
            case 3:
                cnIndex = "四";
                break;
            case 4:
                cnIndex = "五";
                break;
            case 5:
                cnIndex = "六";
                break;
            default:
                cnIndex = "一";
        }
        return cnIndex;
    }

    public static String getAverage(String a, String b) {
        Double d1 = Double.parseDouble(a);
        Double d2 = Double.parseDouble(b);
        DecimalFormat df = new DecimalFormat("#.00");
        double c = d1 / d2;
        if (c > 0 && c < 1) {
            return "0" + df.format(c);
        } else
            return df.format(c);
    }
*/

   /* public static String getMoney(double money) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(money);
    }

    public static String getMoney(int money) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(money);
    }

    public static String getRate(double rate) {
        String r = rate + "";
        int i = r.indexOf(".");
        if (i > -1) {
            String e = r.substring(i);
            float f = Float.parseFloat(e);
            if (f > 0) {
                return r;
            } else return r.substring(0, i);
        } else return r;
    }


    public static String encodePhone(String tel) {
        if (tel.length() < 7) return tel;
        return tel.substring(0, 3) + "****" + tel.substring(7);
    }

    public static void launchWx(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast(context, "您还没有安装微信");
        }
    }

    public static void launchQq(Context context, String qqNo) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNo;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static String getLevelStr(int level) {
        final String[] starLevels = {"全部", "一星级", "二星级", "三星级", "四星级", "五星级"};
        return starLevels[level];
    }

    //判断服务是否处于运行状态.
   public static boolean isServiceRunning(String serviceName, Context context) {
       ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
       for (ActivityManager.RunningServiceInfo info : infos) {
           if (serviceName.equals(info.service.getClassName())) {
               return true;
           }
       }
       return false;
   }
*/



 /*   public static int getNaviHeight(Context context) {
        int result = 0;
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

  //格式化资讯：阅读量、收藏量、点赞量
    public static String getFormatWebCount(int count) {
        String result;

        if (count / 10000 > 0) {
            result = count / 10000 + "w+";
        } else if (count / 1000 > 0) {
            result = count / 1000 + "k+";
        } else {
            result = count + "";
        }
        return result;
    }

    */
/*
    private void getGLESTextureLimitBelowLollipop() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        LogUtil.e("maxSize:" + maxSize[0]);
//        Toast.makeText(this, " " + maxSize[0], Toast.LENGTH_LONG).show();
    }

    private void getGLESTextureLimitEqualAboveLollipop() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {// TROUBLE! No config found.
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL10.EGL_WIDTH, 64,
                EGL10.EGL_HEIGHT, 64,
                EGL10.EGL_NONE
        };
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098; // missing in EGL10
        int[] ctxAttrib = {
                EGL_CONTEXT_CLIENT_VERSION, 1,
                EGL10.EGL_NONE
        };
        EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
        egl.eglMakeCurrent(dpy, surf, surf, ctx);
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy, surf);
        egl.eglDestroyContext(dpy, ctx);
        egl.eglTerminate(dpy);

        LogUtil.e("maxSize:" + maxSize[0]);
//        Toast.makeText(this, " " + maxSize[0], Toast.LENGTH_LONG).show();
    }*/