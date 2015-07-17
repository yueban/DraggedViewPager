package com.bigfat.draggedviewpager.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

public class CustomDragShadowBuilder extends View.DragShadowBuilder {
    View v;
    private float touchX;
    private float touchY;

    public CustomDragShadowBuilder(View v, float touchX, float touchY) {
        super(v);
        this.v = v;
        this.touchX = touchX;
        this.touchY = touchY;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
        canvas.drawBitmap(getBitmapFromView(v), 0, 0, null);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {
        shadowSize.set(v.getWidth(), v.getHeight());
        touchPoint.set(Math.max((int) touchX - 20, 0), Math.max((int) touchY + 20, 0));
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
}