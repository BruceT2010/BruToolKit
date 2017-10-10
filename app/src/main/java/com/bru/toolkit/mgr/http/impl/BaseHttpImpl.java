package com.bru.toolkit.mgr.http.impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.bru.toolkit.R;
import com.bru.toolkit.views.WaitDialog;


/**
 * Class Desc: Http请求功能定义接口
 * <p/>
 * Creator : BruceDing
 * <p/>
 * Email : brucedingdev@foxmail.com
 * Create Time : 2016/7/20 10:20
 */
public abstract class BaseHttpImpl {

    //public void getAsync(String url, Class<?> cls);

    //public void postAsync(String url, RequestBody body, final Class<?> cls);

    private WaitDialog dialog;

    protected void showDialog(Context context) {
        if ((null != dialog) && dialog.isShowing())
            return;
        dialog = new WaitDialog(context, R.style.transprent_dialog);
        dialog.setMessage(R.string.loading_str);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    protected void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private ProgressDialog progress;

    protected void createPreProgress(Context context, int msgResId) {
        if (null == progress) {
            progress = new ProgressDialog(context);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage(context.getResources().getString(msgResId));
            progress.setCancelable(true);
        }
    }

    protected void dismissPreProgress() {
        if (null != progress && progress.isShowing())
            progress.dismiss();
    }

    protected void showPreProgress() {
        if (null != progress && !progress.isShowing())
            progress.show();
    }

    protected void updatePreProgress(int pro) {
        if (null != progress && !progress.isShowing())
            progress.setProgress(pro);
    }

    protected Handler handler = new Handler() {
    };
}
