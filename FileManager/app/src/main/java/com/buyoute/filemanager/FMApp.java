package com.buyoute.filemanager;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;
import com.buyoute.filemanager.tool.FilePath;
import com.buyoute.filemanager.tool.SPHelper;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.io.File;

public class FMApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(true);
        LogUtil.customTagPrefix = "【FileMgr】";

        SPHelper.get().init(this, getPackageName());

        Utils.init(this);
    }

    private static DbManager DBManager;

    public static DbManager getDBManager() {
        if (DBManager == null) {
            initDb();
        }
        return DBManager;
    }

    public static void initDb() {
        try {
            DbManager.DaoConfig privateConfig = new DbManager.DaoConfig()
                    .setDbName("fileMgr.db")
                    .setDbDir(new File(FilePath.getDatabasePath()))
                    .setDbVersion(1)
                    .setDbOpenListener(db -> db.getDatabase().enableWriteAheadLogging())
                    .setDbUpgradeListener((db, oldVersion, newVersion) -> {
                    });
            DBManager = x.getDb(privateConfig);
            LogUtil.e("DB init-ed");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("DB init-err ：" + e.getMessage());
        }
    }
}
