package com.dasend.state.tolch.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.dasend.state.R;


public class SemiCircleProgressBarView extends View {

    private Path mClippingPath;
    private Context mContext;

    float mAngle;

    RectF mOval;

    Paint paint = null;

    public SemiCircleProgressBarView(Context context) {
        super(context);
        mContext = context;
        initilizeImage();
    }

    public SemiCircleProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initilizeImage();
    }

    private void initilizeImage() {
        mClippingPath = new Path();
        paint = new Paint();
    }


    public void setClipping(float progress) {


        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int delta       = metrics.widthPixels * 2;
        int top         = metrics.heightPixels/2 - metrics.widthPixels/2;
        int bottom      = metrics.heightPixels/2 + metrics.widthPixels/2;


        mAngle = 360 - (progress * 360) / 100;
        mClippingPath.reset();
        mOval = new RectF(- delta,  top - delta, metrics.widthPixels + delta, bottom + delta);
        mClippingPath.addCircle(mOval.centerX(), mOval.centerY(), getScreenGridUnit() * 10, Path.Direction.CCW);

        //Redraw the canvas
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Clip the canvas
        canvas.clipPath(mClippingPath, Region.Op.DIFFERENCE);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(getContext().getResources().getColor(R.color.green_light));
        paint.setAlpha((int)(100 + (mAngle * 100/360)));
        canvas.drawArc(mOval, 270, -mAngle, true, paint);
    }

    private float getScreenGridUnit() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels / 32;
    }

}