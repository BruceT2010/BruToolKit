package com.bru.toolkit.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.bru.toolkit.R;

/**
 * Created by Administrator on 2017/11/6.
 */

public abstract class BaseEmptyForFragmentActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_empty_for_fragment;
    }

    @Override
    public void initData() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_for_fragment_fl, getFragment());
        transaction.commit();
    }

    @Override
    public void initUI() {
    }

    @Override
    public void initAction() {
    }

    public abstract Fragment getFragment();
}
