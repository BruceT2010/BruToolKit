package com.bru.toolkit.views.listview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bru.toolkit.R;


/**
 * Class Desc: 封装上拉刷新，下拉加载更多的RecyclerView
 * <p/>
 * Creator : BruceDing
 * <p/>
 * <p/>
 * Create Time : 2016/6/30
 */
public class RefreshRecyclerView extends FrameLayout {

    private String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout refresh_swipeL;
    private TextView refresh_recycler_empty_tv;
    private RecyclerView refresh_recyclerV;
    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isRefreshing = false;
    private boolean isLoadingMore = false;
    private boolean loadMoreEnabled = true;
    private boolean refreshEnabled = true;
    private int emptyHintResId;
    private String emptyHintStr;

    public RefreshRecyclerView(Context context) {
        super(context);
        init();
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.refresh_recycler_layout, this, true);
        refresh_swipeL = (SwipeRefreshLayout) findViewById(R.id.refresh_swipeL);
        refresh_recyclerV = (RecyclerView) findViewById(R.id.refresh_recyclerV);
        refresh_recycler_empty_tv = (TextView) findViewById(R.id.refresh_recycler_empty_tv);
        //默认emptyView
        emptyView = refresh_recycler_empty_tv;
        refresh_swipeL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != onRefreshListener) {
                    onRefreshListener.onRefresh();
                    isRefreshing = true;
                }
            }
        });

        refresh_recyclerV.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisiblePosition = getLastVisiblePosition(recyclerView);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiblePosition + 1 == recyclerView.getAdapter().getItemCount()) {
                    if (null != onLoadMoreListener) {
                        onLoadMoreListener.onLoadMore();
                        isLoadingMore = true;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Logs.e(TAG, "isLoadingMore-->" + isLoadingMore);
//                if (loadMoreEnabled)
//                    if (dy > 0 && getSecondLastColumn(recyclerView) && !isLoadingMore) {
//                        if (null != onLoadMoreListener) {
//                            onLoadMoreListener.onLoadMore();
//                            isLoadingMore = true;
//                        }
//                    }
            }
        });
    }

    private int getLastVisiblePosition(RecyclerView view) {
        int position;
        if (view.getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) view.getLayoutManager()).findLastVisibleItemPosition();
        } else if (view.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) view.getLayoutManager()).findLastVisibleItemPosition();
        } else if (view.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) view.getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = view.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    public int getSpanCount(RecyclerView view) {
        int spanCount;
        if (view.getLayoutManager() instanceof LinearLayoutManager) {
            spanCount = 1;
        } else if (view.getLayoutManager() instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) view.getLayoutManager()).getSpanCount();
        } else if (view.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            spanCount = ((GridLayoutManager) view.getLayoutManager()).getSpanCount();
        } else {
            spanCount = 1;
        }
        return spanCount;
    }

    //倒数第二行
    private boolean getSecondLastColumn(RecyclerView view) {
//        return getLastVisiblePosition(view) == view.getAdapter().getItemCount() - 1 - getSpanCount(view);
//        Logs.e(TAG, "getLastVisiblePosition-->" + getLastVisiblePosition(view) + "\nTogglePosition-->" + (view.getLayoutManager().getItemCount() - getSpanCount(view)) * 4);
        return getLastVisiblePosition(view) == (view.getLayoutManager().getItemCount() - getSpanCount(view) * 4);
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        refresh_recyclerV.setLayoutManager(layoutManager);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        refresh_recyclerV.setItemAnimator(itemAnimator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        refresh_recyclerV.addItemDecoration(decor);
    }

    public void setRefreshing(boolean refreshing) {
        this.isRefreshing = refreshing;
        refresh_swipeL.setRefreshing(refreshing);
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public OnRefreshListener getOnRefreshListener() {
        return onRefreshListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public OnLoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    private View emptyView;
    final private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            Log.e(TAG, "onChanged");
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.e(TAG, "onItemRangeInserted" + itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    private void checkIfEmpty() {
        if (emptyView != null && refresh_recyclerV.getAdapter() != null) {
            final boolean emptyViewVisible = refresh_recyclerV.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            refresh_recyclerV.setVisibility(emptyViewVisible ? INVISIBLE : VISIBLE);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        final RecyclerView.Adapter oldAdapter = refresh_recyclerV.getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        refresh_recyclerV.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        //setAdapter时不检查
        //checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        //checkIfEmpty();
    }

    public boolean isLoadMoreEnabled() {
        return loadMoreEnabled;
    }

    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        this.loadMoreEnabled = loadMoreEnabled;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        refresh_swipeL.setEnabled(refreshEnabled);
        this.refreshEnabled = refreshEnabled;
    }

    public int getEmptyHintResId() {
        return emptyHintResId;
    }

    public void setEmptyHintResId(int emptyHintResId) {
        this.emptyHintResId = emptyHintResId;
        setEmptyHintStr(getContext().getString(emptyHintResId));
    }

    public String getEmptyHintStr() {
        return emptyHintStr;
    }

    public void setEmptyHintStr(String emptyHintStr) {
        this.emptyHintStr = emptyHintStr;
        refresh_recycler_empty_tv.setText(emptyHintStr);
    }
}
