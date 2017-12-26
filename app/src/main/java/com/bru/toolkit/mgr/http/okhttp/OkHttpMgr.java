package com.bru.toolkit.mgr.http.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.bru.toolkit.R;
import com.bru.toolkit.mgr.BruConfig;
import com.bru.toolkit.mgr.http.impl.BaseHttpImpl;
import com.bru.toolkit.mgr.http.impl.HttpImpl;
import com.bru.toolkit.mgr.http.impl.Request4Str;
import com.bru.toolkit.mgr.http.okhttp.coreprogress.helper.ProgressHelper;
import com.bru.toolkit.mgr.http.okhttp.coreprogress.listener.impl.UIProgressListener;
import com.bru.toolkit.utils.Logs;
import com.bru.toolkit.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class Desc: OkHttp请求管理类
 * <p/>
 * Create Time : 2016/7/18 16:32
 */
public class OkHttpMgr extends BaseHttpImpl implements HttpImpl {

    private String TAG = getClass().getSimpleName();
    private static OkHttpMgr instance;

    public static OkHttpMgr getInstance() {
        if (null == instance)
            instance = new OkHttpMgr();
        return instance;
    }

    private OkHttpMgr() {

    }

    @Override
    public void getSync(String url, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str) {
        Logs.e(TAG, url);
        if (showWaitDialog && null != context)
            showDialog(context);
        Request request = new Request.Builder()
                // User-Agent   User-Agent的内容包含发出请求的用户信息    User-Agent: Mozilla/5.0 (Linux; X11)
                //.header("User-Agent", "OkHttp Headers.java")
                // Accept   指定客户端能够接收的内容类型  Accept: text/plain, text/html
                //.addHeader("Accept", "application/json; q=0.5")
                //.addHeader("Accept", "application/vnd.github.v3+json")

                /**
                 * 强制使用网络请求
                 * CacheControl.FORCE_NETWORK和new CacheControl.Builder().noCache().build()是等效的
                 * */
                // or  .cacheControl(CacheControl.FORCE_NETWORK)
                //.cacheControl(new CacheControl.Builder().noCache().build())

                //强制使用缓存
                //.cacheControl(new CacheControl.Builder()
                //       .onlyIfCached()
                //       .build())

                //继续使用过期的缓存(此处设置过期时间为一年)
                //.cacheControl(new CacheControl.Builder()
                //       .maxStale(365, TimeUnit.DAYS)
                //       .build())
                .url(url)
                .build();
        try {
            //请求的执行，取消
            //Call call = yOkHttpClient.newCall(request);
            //call.execute();//执行请求
            //call.cancel();//取消请求

            /**
             * 关于ResponseBody类:
             * 1.ResponseBody存储了服务端发往客户端的原始字节流。
             * 2.ResponseBody必须被关闭
             * 3.每一个响应体都是一个有限的资源支持。如果没有关闭的响应体将泄漏这些资源，
             *   并可能最终导致应用程序的速度慢下来或崩溃。通过close()，bytestream()关闭响应体.reader().close()。
             *   其中bytes()和string()方法会自动关闭响应体。
             * 4.响应主体只能被消耗一次。
             * 5.这个类可以用于非常大的响应流。例如，常见的视频流应用的要求。
             * 6.因为这个ResponseBody不缓冲内存中的全部响应，应用程序不能重新读取响应的字节数。
             *   可以利用 bytes() orstring()，source(), byteStream(), or charStream()等方法，将流内容读入到内存中。
             * */
            Response response = OkHttpHelper.getInstance().getHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                //验证证书
                //for (Certificate certificate : response.handshake().peerCertificates()) {
                //    System.out.println(CertificatePinner.pin(certificate));
                //}
                String result = response.body().string();
                Logs.e(TAG, result);
                if (null != request4Str) {
                    request4Str.onGetStr(result);
                }
                if (!TextUtils.isEmpty(result) && null != cls)
                    EventBus.getDefault().post(new Gson().fromJson(response.body().string(), cls));
            } else {
                Logs.e(TAG, response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postSync(String url, RequestBody body, Class<?> cls, boolean showWaitDialog, Context context, Request4Str request4Str) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Logs.e(TAG, url);
        if (showWaitDialog && null != context)
            showDialog(context);
        try {
            Response response = OkHttpHelper.getInstance().getHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                Logs.e(TAG, result);
                if (null != request4Str) {
                    request4Str.onGetStr(result);
                }
                if (!TextUtils.isEmpty(result) && null != cls)
                    EventBus.getDefault().post(new Gson().fromJson(response.body().string(), cls));
            } else {
                Logs.e(TAG, response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAsync(String url, Class<?> cls) {
        getAsync(url, cls, false, null, null);
    }

    public void getAsync(String url, Class<?> cls, Request4Str listener) {
        getAsync(url, cls, false, null, listener);
    }

    public void getAsync(String url, Request4Str listener) {
        getAsync(url, null, false, null, listener);
    }

    @Override
    public void getAsync(String url, final Class<?> cls, boolean showWaitDialog, Context context, final Request4Str request4Str) {
        Logs.e(TAG, url);
        if (showWaitDialog && null != context)
            showDialog(context);
        Request request = new Request.Builder()
                .header("Cookie", OkHttpHelper.getInstance().getCookieStr())
                .url(url)
                .build();
        OkHttpHelper.getInstance().getHttpClient().newCall(request).enqueue(new Callback() {// 注：该回调是子线程，非主线程
            @Override
            public void onFailure(Call call, IOException e) {
                dismissDialog();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                response.body().close();
                Logs.e(TAG, result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dismissDialog();
                            Logs.e(TAG, result);
                            if (null != request4Str) {
                                request4Str.onGetStr(result);
                            }
                            if (!TextUtils.isEmpty(result) && null != cls) {
                                EventBus.getDefault().post(new Gson().fromJson(result, cls));
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void postAsync(String url, RequestBody body, Class<?> cls, Request4Str listener) {
        postAsync(url, body, cls, false, null, listener);
    }

    @Override
    public void postAsync(String url, RequestBody body, final Class<?> cls, boolean showWaitDialog, Context context, final Request4Str request4Str) {
        final Request request = new Request.Builder()
                .header("Cookie", OkHttpHelper.getInstance().getCookieStr())
                .url(url)
                .post(body)
                .build();
        Logs.e(TAG, url);
        if (showWaitDialog && null != context)
            showDialog(context);
        OkHttpHelper.getInstance().getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String result = response.body().string();
                    Logs.e(TAG, result);
                    HttpUrl loginUrl = request.url();
                    if (loginUrl.toString().equals("http://www.inklego.com/api/v1/user/login")) {
                        List<Cookie> cookies = new ArrayList<>();
                        Headers headers = response.headers();
                        cookies = Cookie.parseAll(loginUrl, headers);
                        StringBuilder cookieStr = new StringBuilder();
                        for (Cookie cookie : cookies) {
                            if (cookie.name().equals("intoken") && !cookie.value().equals("deleted")) {
                                cookieStr.append(cookie.name()).append("=").append(cookie.value());
                            }
                        }
                        SPUtils.put(BruConfig.AppContext(), OkHttpHelper.COOKIE_STR, cookieStr);
                        OkHttpHelper.getInstance().setCookieStr(cookieStr.toString());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            Logs.e(TAG, result);
                            if (null != request4Str) {
                                request4Str.onGetStr(result);
                            }
                            if (!TextUtils.isEmpty(result) && null != cls)
                                EventBus.getDefault().post(new Gson().fromJson(result, cls));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postAsync(String url, RequestBody body, Request4Str listener) {
        postAsync(url, body, null, false, null, listener);
    }

    @Override
    public void postAsync(String url, RequestBody body, Class<?> cls) {
        postAsync(url, body, cls, false, null, null);
    }

    @Override
    public void download(Context context, String url) {
        if (null != context) {
            createPreProgress(context, R.string.downloading);
        }
        //这个是非ui线程回调，不可直接操作UI
//        final ProgressListener progressResponseListener = new ProgressListener() {
//            @Override
//            public void onProgress(long bytesRead, long contentLength, boolean done) {
//                Logs.e("TAG", "bytesRead:" + bytesRead);
//                Logs.e("TAG", "contentLength:" + contentLength);
//                Logs.e("TAG", "done:" + done);
//                if (contentLength != -1) {
//                    //长度未知的情况下回返回-1
//                    Logs.e("TAG", (100 * bytesRead) / contentLength + "% done");
//                }
//                Logs.e("TAG", "================================");
//            }
//        };

        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressResponseListener = new UIProgressListener() {
            @Override
            public void onUIProgress(long bytesRead, long contentLength, boolean done) {
                Logs.e("TAG", "bytesRead:" + bytesRead);
                Logs.e("TAG", "contentLength:" + contentLength);
                Logs.e("TAG", "done:" + done);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Logs.e("TAG", (100 * bytesRead) / contentLength + "% done");
                }
                Logs.e("TAG", "================================");
                //ui层回调
                updatePreProgress((int) ((100 * bytesRead) / contentLength));
            }

            @Override
            public void onUIStart(long bytesRead, long contentLength, boolean done) {
                super.onUIStart(bytesRead, contentLength, done);
                showPreProgress();
            }

            @Override
            public void onUIFinish(long bytesRead, long contentLength, boolean done) {
                super.onUIFinish(bytesRead, contentLength, done);
                dismissPreProgress();
            }
        };

        //构造请求
        final Request request = new Request.Builder()
                .url(url)
                .build();

        //包装Response使其支持进度回调
        ProgressHelper.addProgressResponseListener(OkHttpHelper.getInstance().getHttpClient(), uiProgressResponseListener).newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dismissPreProgress();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logs.e(TAG, response.body().string());
                dismissPreProgress();
            }
        });
    }

    @Override
    public void upload(Context context, RequestBody requestBody, String url, final Class<?> cls, final Request4Str request4Str) {
        if (null != context)
            createPreProgress(context, R.string.uploading);

        //这个是非ui线程回调，不可直接操作UI
//        final ProgressListener progressListener = new ProgressListener() {
//            @Override
//            public void onProgress(long bytesWrite, long contentLength, boolean done) {
//                Logs.e("TAG", "bytesWrite:" + bytesWrite);
//                Logs.e("TAG", "contentLength" + contentLength);
//                Logs.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
//                Logs.e("TAG", "done:" + done);
//                Logs.e("TAG", "================================");
//            }
//        };

        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
            @Override
            public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
                Logs.e("TAG", "bytesWrite:" + bytesWrite);
                Logs.e("TAG", "contentLength" + contentLength);
                Logs.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
                Logs.e("TAG", "done:" + done);
                Logs.e("TAG", "================================");
                //ui层回调
                updatePreProgress((int) ((100 * bytesWrite) / contentLength));
                //Toast.makeText(getApplicationContext(), bytesWrite + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUIStart(long bytesWrite, long contentLength, boolean done) {
                super.onUIStart(bytesWrite, contentLength, done);
                showPreProgress();
            }

            @Override
            public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
                super.onUIFinish(bytesWrite, contentLength, done);
                dismissPreProgress();
            }
        };

        //构造上传请求，类似web表单
//        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("hello", "android")
//                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), file))
//                .build();

        /**
         * 构建表单FormBody:
         * 继承了RequestBody类，并内置了MediaType类型，用且用集合存储键值对数据
         * */
//        FormBody formBody = new FormBody.Builder()
//                .add("search", "biezhihua")
//                .build();

        //上传流
//        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
//        RequestBody requestBody = new RequestBody() {
//
//            @Override
//            public MediaType contentType() {
//                return MEDIA_TYPE_MARKDOWN;
//            }
//
//            @Override
//            public void writeTo(BufferedSink sink) throws IOException {
//                sink.writeUtf8("Numbers\n");
//                sink.writeUtf8("-------\n");
//                for (int i = 2; i <= 997; i++) {
//                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
//                }
//            }
//
//            private String factor(int n) {
//                for (int i = 2; i < n; i++) {
//                    int x = n / i;
//                    if (x * i == n) return factor(x) + " × " + i;
//                }
//                return Integer.toString(n);
//            }
//        };

        Request request = new Request.Builder()
                .header("Cookie", OkHttpHelper.getInstance().getCookieStr())
                //上传小文件
                //.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))

                //上传表单
                //.post(formBody)
                .url(url)
                .post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener)).build();
        //开始请求
        OkHttpHelper.getInstance().getHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dismissPreProgress();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Logs.e(TAG, result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        Logs.e(TAG, result);
                        if (null != request4Str) {
                            request4Str.onGetStr(result);
                        }
                        if (!TextUtils.isEmpty(result) && null != cls)
                            EventBus.getDefault().post(new Gson().fromJson(result, cls));
                    }
                });
                dismissPreProgress();
            }
        });

    }


    public void upload(RequestBody requestBody, String url, final Class<?> cls, final Request4Str request4Str) {
        Request request = new Request.Builder()
                .header("Cookie", OkHttpHelper.getInstance().getCookieStr())
                .url(url)
                .post(requestBody)
                .build();

        OkHttpHelper.getInstance().getHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dismissPreProgress();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Logs.e(TAG, result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        Logs.e(TAG, result);
                        if (null != request4Str) {
                            request4Str.onGetStr(result);
                        }
                        if (!TextUtils.isEmpty(result) && null != cls)
                            EventBus.getDefault().post(new Gson().fromJson(result, cls));
                    }
                });
                dismissPreProgress();
            }
        });
    }
}
