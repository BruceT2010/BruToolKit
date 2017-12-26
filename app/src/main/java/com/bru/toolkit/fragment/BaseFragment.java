package com.bru.toolkit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bru.toolkit.pojo.BaseResponseBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 
 * Class Desc： Fragment基类
 * 
 * @Creator： BruceDing
 * @Create Time：2015年5月5日 上午11:25:13
 * 
 */
public abstract class BaseFragment extends Fragment {

	protected String TAG =getClass().getSimpleName();
//	protected FragmentListener fragmentListener;
	protected View mainView;

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//			fragmentListener = (FragmentListener) activity;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainView = inflater.inflate(getMainLayout(), container, false);
		initUI();
		return mainView;
	}

	@Subscribe
	public void onEventMainThread(BaseResponseBean responseBean) {
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		initAction();
		initData();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if (EventBus.getDefault().isRegistered(this))
				EventBus.getDefault().unregister(this);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (EventBus.getDefault().isRegistered(this))
				EventBus.getDefault().unregister(this);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * 布局文件
	 * 
	 * @return layout id
	 */
	public abstract int getMainLayout();

	/**
	 * 控件初始化
	 * 
	 */
	public abstract void initUI();

	/**
	 * 事件监听
	 */
	public abstract void initAction();

	/**
	 * 数据处理
	 */
	public abstract void initData();

}
