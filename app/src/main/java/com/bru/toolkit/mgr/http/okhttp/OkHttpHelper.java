package com.bru.toolkit.mgr.http.okhttp;

import android.util.Log;

import com.bru.toolkit.mgr.BruConfig;
import com.bru.toolkit.utils.SPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/11/20.
 */

public class OkHttpHelper {

    private static OkHttpHelper instance;
    public static String COOKIE_STR = "COOKIE_STR";

    private OkHttpHelper() {
        cookieStr = (String) SPUtils.get(BruConfig.AppContext(), COOKIE_STR, "");
    }

    public static OkHttpHelper getInstance() {
        if (null == instance) {
            instance = new OkHttpHelper();
        }
        return instance;
    }

    private int TIME_OUT = 50;

    public OkHttpClient getHttpClient() {
        if (isCookieUnify()) {
            return getCookieJarHttpClient();
        } else {
            return getNormalHttpClient();
        }
    }

    private OkHttpClient mOkHttpClient, cOkHttpClient;

    public OkHttpClient getNormalHttpClient() {
        //缓存文件大小
        //int cacheSize = 10 * 1024 * 1024;
        //指定缓存文件
        //Cache cache = new Cache(new File("bzh.tmp"), cacheSize);
        if (null == mOkHttpClient) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    //添加日志拦截器
                    //.addInterceptor(new LoggingInterceptor())
                    /**
                     * 授权证书:
                     * 通过Authenticator类，可以响应来自远程或者代理服务器的授权验证，
                     * 通常情况会返回一个授权头以做验证；亦或是返回空表示拒绝验证。
                     * 简单来说，你要访问一个服务，但是你要对方的验证。通过Authenticator类来代理一个认证请求，
                     * 并使用Credentials.basic()来构造一个证书。
                     * */
//                .authenticator(new Authenticator() {
//                    @Override
//                    public Request authenticate(Route route, Response response) throws IOException {
//                        System.out.println("Authenticating for response: " + response);
//                        System.out.println("Challenges: " + response.challenges());
//                        String credential = Credentials.basic("jesse", "password1");
//                        // HTTP授权的授权证书  Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
//                        return response
//                                .request()
//                                .newBuilder()
//                                .header("Authorization", credential)
//                                .build();
//                    }
//                })

                    /**
                     * 缓存HTTP和HTTPS响应文件，所以他们可以重复使用，节省时间和带宽。
                     * OkHttp默认没有缓存功能
                     * */
                    //.cache(cache)

                    //证书
//                .certificatePinner(new CertificatePinner.Builder()
//                        .add("publicobject.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=")
//                        .add("publicobject.com", "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=")
//                        .add("publicobject.com", "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=")
//                        .add("publicobject.com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
//                        .build())
                    .build();
        }
        return mOkHttpClient;
    }

    public OkHttpClient getCookieJarHttpClient() {
        if (null == cOkHttpClient) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.cookieJar(new CookieJar() {
//                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
//
//                @Override
//                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                    if (url.toString().equals("http://www.inklego.com/api/v1/user/login")) {
//                        for (int i = 0; i < cookies.size(); i++) {
//                            if (cookies.get(i).name().equals("intoken")) {
//                                setCookieStr("intoken=" + cookies.get(i).value());
//                                Log.e(getClass().getSimpleName(), getCookieStr());
//                            }
//                        }
//                        cookieStore.put(url.host(), cookies);
//                    }
//                }
//
//                @Override
//                public List<Cookie> loadForRequest(HttpUrl url) {
//                    if (!url.toString().equals("http://www.inklego.com/api/v1/user/login")) {
//                        List<Cookie> cookies = cookieStore.get(url.host());
//                        return cookies != null ? cookies : new ArrayList<Cookie>();
//                    }
//                    return Collections.emptyList();
//                }
//            });
            builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
            builder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
            cOkHttpClient = builder.build();
        }
        return cOkHttpClient;
    }

    private boolean cookieUnify = false;

    public boolean isCookieUnify() {
        return cookieUnify;
    }

    public void setCookieUnify(boolean cookieUnify) {
        this.cookieUnify = cookieUnify;
    }

    private String cookieStr = "";

    public String getCookieStr() {
        return cookieStr;
    }

    public void setCookieStr(String cookieStr) {
        this.cookieStr = cookieStr;
    }
}
