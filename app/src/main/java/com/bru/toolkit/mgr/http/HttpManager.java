package com.bru.toolkit.mgr.http;

import android.content.Context;


import com.bru.toolkit.mgr.http.impl.HttpImpl;
import com.bru.toolkit.mgr.http.impl.Request4Str;

import okhttp3.RequestBody;

/**
 * Class Desc: http请求管理类
 * <p/>
 * Creator : DingJian
 * <p/>
 * Email : jian.ding@melot.cn
 * Create Time : 2016/8/15 18:47
 */
public class HttpManager implements HttpImpl {

    private HttpManager() {
        //httpImpl = new OkHttpMgr();
    }

    private HttpImpl httpImpl;
    private static HttpManager instance;

    public static HttpManager getInstance() {
        if (null == instance)
            instance = new HttpManager();
        return instance;
    }


    @Override
    public void getSync(String url, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str) {
        httpImpl.getSync(url, cls, showWaitDialog, context, request4Str);
    }

    @Override
    public void postSync(String url, RequestBody body, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str) {
        httpImpl.postSync(url, body, cls, showWaitDialog, context, request4Str);
    }

    @Override
    public void getAsync(String url, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str) {
        httpImpl.getAsync(url, cls, showWaitDialog, context, request4Str);
    }

    @Override
    public void postAsync(String url, RequestBody body, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str) {
        httpImpl.postSync(url, body, cls, showWaitDialog, context, request4Str);
    }

    @Override
    public void postAsync(String url, RequestBody body, Class<?> cls) {
        httpImpl.postAsync(url, body, cls);
    }

    @Override
    public void upload(Context context, RequestBody requestBody, String url, Class<?> cls, Request4Str request4Str) {
        httpImpl.upload(context, requestBody, url, cls, request4Str);
    }

    @Override
    public void download(Context context, String url) {
        httpImpl.download(context, url);
    }

    public void getAsync(String url, Class<?> cls) {
        getAsync(url, cls, false, null, null);
    }

    public void getAsync(String url, Class<?> cls, boolean showWaitDialog, Context context) {
        getAsync(url, cls, showWaitDialog, context, null);
    }

    public void getStrAsync(String url, Request4Str request4Str) {
        getAsync(url, null, false, null, request4Str);
    }

    public void getStrAsync(String url, boolean showWaitDialog, Context context, Request4Str request4Str) {
        getAsync(url, null, showWaitDialog, context, request4Str);
    }

    public void upload(Context context, RequestBody requestBody, String url, Request4Str request4Str) {
        upload(context, requestBody, url, null, request4Str);
    }
}
