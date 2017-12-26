package com.bru.toolkit.mgr.http.okhttp;


import com.bru.toolkit.utils.Logs;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class Desc: OkHttp日志拦截器
 * <p/>
 * Creator : DingJian
 * <p/>
 * Email : jian.ding@melot.cn
 * <p/>
 * Create Time: 2016/08/24 17:24
 */
public class LoggingInterceptor implements Interceptor {

    private String TAG = getClass().getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        //Request
        long t1 = System.nanoTime();
        Request request = chain.request();
        Logs.e(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        //Response
        long t2 = System.nanoTime();
        Response response = chain.proceed(request);
//        Logs.e(String.format("Received response for %s in %.1fms%n%s", request.url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}
