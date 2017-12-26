package com.bru.toolkit.views.viewpager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class BannerViewPager extends ViewPager implements OnGestureListener {

	/** 手势滑动处理类 **/
	private GestureDetector mDetector;

	public BannerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		GestureDetector detector = new GestureDetector(context, this);
		mDetector = detector;
		// setTransitionEffect(TransitionEffect.Tablet);
	}

	public GestureDetector getGestureDetector() {
		return mDetector;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (listener != null) {
			listener.setOnSimpleClickListenr(getCurrentItem());
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	private OnSimpleClickListener listener;

	/** 单击监听接口 **/
	public interface OnSimpleClickListener {
		void setOnSimpleClickListenr(int position);
	}

	public void setOnSimpleClickListener(OnSimpleClickListener listener) {
		this.listener = listener;
	}
}
