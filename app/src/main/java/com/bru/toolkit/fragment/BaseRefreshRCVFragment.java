package com.bru.toolkit.fragment;

import com.bru.toolkit.R;
import com.bru.toolkit.views.listview.RefreshRecyclerView;

/**
 * Class Desc: Class Desc
 * <p/>
 * Creator : BruceDing
 * <p/>
 * Create Time : 2016/8/3 10:02
 */
public abstract class BaseRefreshRCVFragment extends BaseFragment implements RefreshRecyclerView.OnRefreshListener, RefreshRecyclerView.OnLoadMoreListener {

    protected RefreshRecyclerView global_refreshRecView;
    protected boolean doRefresh = false;

    @Override
    public void initAction() {
        //global_refreshRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //global_refreshRecView.setItemAnimator(new DefaultItemAnimator());
        //分割线
        //global_refreshRecView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        global_refreshRecView.setOnRefreshListener(this);
        global_refreshRecView.setOnLoadMoreListener(this);
    }

    @Override
    public void initUI() {
        global_refreshRecView = (RefreshRecyclerView) mainView.findViewById(R.id.global_refreshRecView);
    }

    @Override
    public int getMainLayout() {
        return R.layout.global_recycler_layout;
    }

    @Override
    public void onRefresh() {
        doRefresh = true;
        doRefresh();
    }

    @Override
    public void onLoadMore() {
        doLoadMore();
    }

    public abstract void doLoadMore();

    public abstract void doRefresh();

    protected void onRefreshDone() {
        doRefresh = false;
        global_refreshRecView.setRefreshing(doRefresh);
    }

}
