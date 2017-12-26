package com.bru.toolkit.mgr;

import android.content.Context;

/**
 * Class Desc: 工具箱配置
 * <p/>
 * Creator : Bruce Ding
 * <p/>
 * Email : brucedingdev@foxmail.com
 * <p/>
 * Create Time: 2017/03/17 11:20
 */
public class BruConfig {

    public static boolean isDebug = true;
    private static Context appContext;

    public static Context AppContext() {
        return appContext;
    }

    public static void setContext(Context context) {
        appContext = context;
    }

}