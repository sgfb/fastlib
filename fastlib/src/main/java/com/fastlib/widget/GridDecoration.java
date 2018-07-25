package com.fastlib.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/11/25.
 * RecyclerView的Grid布局间隔
 */
public class GridDecoration extends RecyclerView.ItemDecoration{
    private int mDividerWidth,mDividerHeight;
    private boolean isDividerLeft,isDividerRight,isDividerTop,isDividerBottom;  //上下左右顶部是否间隔

    public GridDecoration(int mDividerWidth, int mDividerHeight) {
        this.mDividerWidth = mDividerWidth;
        this.mDividerHeight = mDividerHeight;
    }

    public GridDecoration(int mDividerWidth, int mDividerHeight, boolean roundDivider){
        this.mDividerWidth = mDividerWidth;
        this.mDividerHeight = mDividerHeight;
        setRoundDivider(roundDivider);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        if(parent.getLayoutManager() instanceof GridLayoutManager){
            GridLayoutManager layoutManager= (GridLayoutManager) parent.getLayoutManager();
            int top=mDividerHeight/2,left=mDividerWidth/2;
            int bottom=top,right=left;
            int position=parent.getChildAdapterPosition(view);

            //最左边一列
            if(position%layoutManager.getSpanCount()==0){
                if(isDividerLeft)
                    left*=2;
                else left=0;
            }
            //最右边一列
            if(position%layoutManager.getSpanCount()==layoutManager.getSpanCount()-1){
                if(isDividerRight)
                    right*=2;
                else right=0;
            }
            //第一排
            if(position<layoutManager.getSpanCount()){
                if(isDividerTop)
                    top*=2;
                else top=0;
            }
            if(position>=parent.getAdapter().getItemCount()-layoutManager.getSpanCount()){
                if(isDividerBottom)
                    bottom*=2;
                else bottom=0;
            }
            outRect.set(left,top,right,bottom);
        }
    }

    public void setRoundDivider(boolean roundDivider){
        isDividerLeft=roundDivider;
        isDividerTop=roundDivider;
        isDividerRight=roundDivider;
        isDividerBottom=roundDivider;
    }

    public void setLeftDivider(boolean isLeftDivider){
        isDividerLeft=isLeftDivider;
    }

    public void setRightDivider(boolean isRightDivider){
        isDividerRight=isRightDivider;
    }

    public void setTopDivider(boolean isTopDivider){
        isDividerTop=isTopDivider;
    }

    public void setBottomDivider(boolean isBottomDivider){
        isDividerBottom=isBottomDivider;
    }
}