package com.bru.toolkit.utils;

import android.graphics.drawable.Drawable;
import android.widget.RadioButton;

/**
 * Class Desc: Class Desc
 * <p>
 * Creator : Bruce Ding
 * <p>
 * Email : brucedingdev@foxmail.com
 * <p>
 * Create Time: 2017/10/12 16:24
 */
public class ViewUtil {

    public static void setRdoDrawableBound(RadioButton radioButton) {
        Drawable[] drawables = radioButton.getCompoundDrawables();
        drawables[1].setBounds(0, 0, 80, 80);//四个参数分别为left,top,right,bottom
        radioButton.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

}
