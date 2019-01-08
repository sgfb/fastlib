package com.fastlib.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sgfb on 16/8/23.
 * RecyclerView的垂直间隔 TODO
 */
public class LinearDecoration extends RecyclerView.ItemDecoration {
    private int mLength;
    private boolean isBound;
    private Drawable mDividerDrawable;

    public LinearDecoration(int mLength) {
        this.mLength = mLength;
    }

    public LinearDecoration(int mLength, boolean isBound) {
        this.mLength = mLength;
        this.isBound = isBound;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            int bound = isBound && parent.getChildAdapterPosition(view) == 0 ? mLength : 0;
            if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL)
                outRect.set(0, bound, 0, mLength);
            else outRect.set(bound, 0, mLength, 0);
        }
    }

    public void setLength(int mLength) {
        this.mLength = mLength;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDividerDrawable != null)
            mDividerDrawable.draw(c);
    }

    public void setDrawable(Drawable drawable){
        mDividerDrawable=drawable;
    }
}