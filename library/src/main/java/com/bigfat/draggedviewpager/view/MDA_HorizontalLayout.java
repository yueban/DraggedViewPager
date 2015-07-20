package com.bigfat.draggedviewpager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bigfat.draggedviewpager.utils.DragUtils;


/**
 * Created by yueban on 10/7/15.
 */
public class MDA_HorizontalLayout extends ViewGroup {
    private static final String TAG = MDA_HorizontalLayout.class.getSimpleName();

    public MDA_HorizontalLayout(Context context) {
        this(context, null);
    }

    public MDA_HorizontalLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MDA_HorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setPadding(DragUtils.pageMargin + DragUtils.pageEdgeVisibleWidth, 0, DragUtils.pageMargin + DragUtils.pageEdgeVisibleWidth, 0);

        int pageWidth = DragUtils.pageScrollWidth - DragUtils.pageMargin * 2;//每一页的宽度

        int width = 0;
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            LayoutParams lp = (LayoutParams) childView.getLayoutParams();
            lp.width = pageWidth - lp.leftMargin - lp.rightMargin;

            //子View占据的宽度
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + DragUtils.pageMargin * 2;
            //子View占据的高度
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //计算宽高
            width += childWidth;
            height = Math.max(height, childHeight);
        }
        //根据测量模式设置宽高
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(),
                height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            //判断View显示状态
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            LayoutParams lp = (LayoutParams) childView.getLayoutParams();
            int cLeft = left + lp.leftMargin + DragUtils.pageMargin;
            int cTop = top + lp.topMargin;
            int cRight = cLeft + childView.getMeasuredWidth();
            int cBottom = cTop + childView.getMeasuredHeight();
            //布局
            childView.layout(cLeft, cTop, cRight, cBottom);
            left += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + DragUtils.pageMargin * 2;
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    // 继承自margin，支持子视图android:layout_margin属性
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }
}