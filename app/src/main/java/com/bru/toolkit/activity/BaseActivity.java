package com.bru.toolkit.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.bru.toolkit.pojo.BaseResponseBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Class Desc： Activity基类
 * <p/>
 * @Creator： BruceDing
 * <p/>
 * Email : brucedingdev@foxmail.com
 * <p/>
 * @Create Time：2015年5月5日 上午11:24:46
 */
public abstract class BaseActivity extends FragmentActivity {

    protected String TAG = getClass().getSimpleName();

    protected int eventCode = -1;
    protected Handler handler = new Handler() {
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EventBus.getDefault().register(this);
        // No Titlebar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayout());
		initUI();
        initAction();
        initData();

        // Full Screen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Subscribe
    public void onEventMainThread(BaseResponseBean baseResponseBean) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 获取布局文件
     * **/
	public abstract int getLayout();

    /**
     * 初始化页面组件
     * **/
	public abstract void initUI();

    /**
     * 初始化事件
     **/
    public abstract void initAction();

    /**
     * 初始化数据
     **/
    public abstract void initData();

    /**
     * back键
     */
    //public abstract void onFinishEvent();

    public void setTitle(int resId) {
        setTitle(getResources().getString(resId));
    }

    /**
     * 添加Fragment
     */
    protected void addFragment(int layoutId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(layoutId, fragment);
        transaction.commit();
    }

    /**
     * 返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onFinishEvent();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
     */
}
