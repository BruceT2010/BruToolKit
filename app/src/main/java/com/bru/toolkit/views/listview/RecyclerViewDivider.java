package com.bru.toolkit.views.listview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 为RecyclerView增加间距
 * 预设2列，如果是3列，则左右值不同
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
    private int space = 0;
    private int col = 0;
    private int headerCount = 0;

    public RecyclerViewDivider(int space, int col, int headerCount) {
        this.space = space;
        this.col = col;
        this.headerCount = headerCount;
    }

    public RecyclerViewDivider(int space, int col) {
        this.space = space;
        this.col = col;
        this.headerCount = 0;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //该View在整个RecyclerView中位置。
        int pos = parent.getChildAdapterPosition(view);

        if (pos != 0) {
            outRect.top = space;
        }

        int childCount = pos - headerCount;
        //两列的左边一列
        if (childCount % col == 0) {
            outRect.left = space;
            outRect.right = space / 2;
        } else if (childCount % col == col - 1) { //两列的右边一列
            outRect.left = space / 2;
            outRect.right = space;
        } else {
            outRect.left = space / 2;
            outRect.right = space / 2;
        }
    }
}