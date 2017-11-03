package com.dasend.state.tolch.views;


import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {


    private int mPadding;

    public SimpleDividerItemDecoration(Context context, int verticalSpaceHeight) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, verticalSpaceHeight, metrics);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {


        outRect.bottom = mPadding;
        outRect.right = mPadding * 2;
        outRect.left = mPadding * 2;
    }
}