package com.bru.toolkit.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Class Desc: 系统功能工具类
 * <p/>
 * Creator : BruceDing
 * <p/>
 * Email : jian.ding@melot.cn
 * <p/>
 * Create Time: 2017/03/16 17:51
 */
public class SystemFuncUtil {

    /**
     * 强制隐藏键盘
     */
    protected void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 强制显示键盘
     */
    protected void showKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 键盘Toggle
     */
    protected void toggleKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 键盘是否弹起
     */
    protected boolean isKeyBoardActive(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();//isOpen若返回true，则表示输入法打开
    }

    /**
     * 拨打电话
     *
     * @param phone 手机号码
     */
    public void makePhoneCall(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }
}
