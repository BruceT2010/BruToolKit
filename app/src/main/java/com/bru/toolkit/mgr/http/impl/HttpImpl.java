package com.bru.toolkit.mgr.http.impl;

import android.content.Context;

import okhttp3.RequestBody;

/**
 * Class Desc: http请求管理器
 * <p/>
 * Creator : DingJian
 * <p/>
 * Email : jian.ding@melot.cn
 * Create Time : 2016/8/15 18:24
 */
public interface HttpImpl {

    /**
     * 同步GET请求
     *
     * @param url            请求地址
     * @param cls            返回类型
     * @param showWaitDialog 显示等待层
     * @param context        Context对象
     * @param request4Str    str返回接口
     */

    public void getSync(String url, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str);

    /**
     * 同步Post请求
     *
     * @param url            请求地址
     * @param body           post请求参数
     *                       RequestBody body = new FormEncodingBuilder()
     *                       .add("platform", "android")
     *                       .add("name", "bug")
     *                       .build();
     * @param cls            返回数据class
     * @param showWaitDialog 显示等待层
     * @param context        Context对象
     * @param request4Str    str返回接口
     */

    public void postSync(String url, RequestBody body, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str);

    /**
     * 异步GET请求
     *
     * @param url            请求地址
     * @param cls            返回数据class
     * @param showWaitDialog 显示等待层
     * @param context        等待层所在context
     */
    public void getAsync(String url, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str);

    /**
     * 异步Post请求
     *
     * @param url  请求地址
     * @param body post请求参数
     *             RequestBody body = new FormBody.Builder()
     *             .add("platform", "android")
     *             .add("name", "bug")
     *             .build();
     * @param cls  返回数据class
     */
    public void postAsync(String url, RequestBody body, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str);


    /**
     * 异步Post请求
     *
     * @param url  请求地址
     * @param body post请求参数
     *             RequestBody body = new FormBody.Builder()
     *             .add("platform", "android")
     *             .add("name", "bug")
     *             .build();
     * @param cls  返回数据class
     */
    public void postAsync(String url, RequestBody body, Class<?> cls);

    /**
     * 文件上传
     *
     * @param context     Context对象
     * @param requestBody 参数包装
     * @param url         上传文件路径
     * @param cls         返回类型
     * @param request4Str str返回接口
     */
    public void upload(Context context, RequestBody requestBody, String url, Class<?> cls, Request4Str request4Str);

    /**
     * 文件下载
     *
     * @param context Context对象
     * @param url     文件下载地址
     */
    public void download(Context context, String url);
}
