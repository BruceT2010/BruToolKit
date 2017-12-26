package com.bru.toolkit.mgr.http.retrofit;



import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by DJ on 2016/6/27.
 */
public class RetrofitManager {

    private Retrofit mRetrofit;

    private volatile static RetrofitManager instance;

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (null == instance) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        if (null == mRetrofit) {
            mRetrofit = RetrofitManager.getInstance().initRetrofit();
        }
        return mRetrofit;
    }

    private Retrofit initRetrofit() {
        return mRetrofit;
    }
}
