package com.bru.toolkit.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bru.toolkit.R;

/**
 * Class Desc: 等待框处理，触摸不消失
 * <p/>
 * Creator : Bruce Ding
 * <p/>
 * Email : brucedingdev@foxmail.com
 * <p/>
 * Create Time: 2017/03/17 11:30
 */
public class WaitDialog extends AlertDialog {

    public WaitDialog(Context context, int theme) {
        super(context, theme);
    }

    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitdialog_layout);
        setScreenBrightness();
        TextView waitdioag_loading_txt = (TextView) findViewById(R.id.waitdioag_loading_txt);
        waitdioag_loading_txt.setText(msg);
        ImageView image = (ImageView) WaitDialog.this.findViewById(R.id.waitdioag_loading_img);
        // 旋转图片，等待旋转按钮
        Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(550);

        anim.setInterpolator(new LinearInterpolator());
        image.startAnimation(anim);
    }

    private void setScreenBrightness() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = (float) 0.5;
        // lp.alpha = 10;
        window.setAttributes(lp);
    }

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		return false;
//	}

    public void setMessage(String message) {
        this.msg = message;
    }

    public void setMessage(int resId) {
        setMessage(getContext().getResources().getString(resId));
    }
}