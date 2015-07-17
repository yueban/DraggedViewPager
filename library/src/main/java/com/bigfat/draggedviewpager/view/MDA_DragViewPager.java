package com.bigfat.draggedviewpager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigfat.draggedviewpager.R;
import com.bigfat.draggedviewpager.model.Page;
import com.bigfat.draggedviewpager.utils.DragUtils;
import com.bigfat.draggedviewpager.utils.MDA_DragViewPagerController;
import com.bigfat.draggedviewpager.utils.MDA_DragViewPagerListener;

import java.util.ArrayList;


/**
 * Created by yueban on 10/7/15.
 */
public class MDA_DragViewPager extends HorizontalScrollView {
    public static final String TAG = MDA_DragViewPager.class.getSimpleName();

    private MDA_DragViewPagerListener dragViewPagerListener;

    //控件
    private MDA_HorizontalLayout container;//布局控件
    private MDA_DragViewPagerController controller;
    private int currentPage = 0;//当前页Index
    private int pageSwitchOffsetX;//触发页面切换的X轴偏移量
    private int pageSwitchSpeed;//触发页面切换的速度
    private float touchStartX;//触摸起始X坐标
    private long touchStartTime;//触摸起始时间

    public MDA_DragViewPager(Context context) {
        this(context, null);
    }

    public MDA_DragViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MDA_DragViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DragUtils.screenWidth = getResources().getDisplayMetrics().widthPixels;
        DragUtils.screenHeight = getResources().getDisplayMetrics().heightPixels;
        DragUtils.pageEdgeVisibleWidth =getResources().getDimensionPixelOffset(R.dimen.page_edge_visible_width);
        DragUtils.pageMargin = getResources().getDimensionPixelOffset(R.dimen.page_margin);
        DragUtils.pageScrollWidth = DragUtils.screenWidth - DragUtils.pageMargin * 2 - DragUtils.pageEdgeVisibleWidth * 2;

        pageSwitchOffsetX = getResources().getDimensionPixelSize(R.dimen.page_switch_offset_x);
        pageSwitchSpeed = getResources().getDimensionPixelSize(R.dimen.page_switch_speed);

        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        container = new MDA_HorizontalLayout(getContext());
        addView(container);
    }

    public ArrayList<Page> getData() {
        return controller.getData();
    }

    public MDA_DragViewPagerController getController() {
        return controller;
    }

    public <T> void setController(MDA_DragViewPagerController<T> controller) {
        this.controller = controller;
        controller.setDragViewPager(this);

        initView(controller);
    }

    public void setDragViewPagerListener(MDA_DragViewPagerListener dragViewPagerListener) {
        this.dragViewPagerListener = dragViewPagerListener;
    }

    /**
     * 设置页面交换响应延迟
     *
     * @param pageSwapDelay 响应值（ms）
     */
    public void setPageSwapDelay(int pageSwapDelay) {
        DragUtils.pageSwapDelay = pageSwapDelay;
    }

    /**
     * 设置item移动响应延迟
     *
     * @param itemMoveDelay 响应值（ms）
     */
    public void setItemMoveDelay(int itemMoveDelay) {
        DragUtils.itemMoveDelay = itemMoveDelay;
    }

    public <T> void initView(MDA_DragViewPagerController<T> adapter) {
        int currentPageIndex = currentPage;

        container.removeAllViews();

        for (int i = 0; i < adapter.getData().size(); i++) {
            View pageView = LayoutInflater.from(getContext()).inflate(R.layout.item_page, container, false);
            //添加View
            container.addView(pageView);
            TextView tvTitle = (TextView) pageView.findViewById(R.id.tv_item_page_title);
            ScrollView scrollView = (ScrollView) pageView.findViewById(R.id.sv_item_page);
            MDA_PageListLayout<T> pageListLayout = new MDA_PageListLayout<T>(getContext(), adapter.getItemLayoutRes());
            //添加View
            scrollView.addView(pageListLayout);
            //设置页面索引
            pageListLayout.setPageIndex(i);
            //设置数据
            Page<T> page = adapter.getData().get(i);
            tvTitle.setText(page.getTitle());
            pageListLayout.setData(page.getData());
        }

        initDragEvent(DragUtils.DragViewType.ALL);

        scrollToPage(currentPageIndex);
    }

    public MDA_HorizontalLayout getContainer() {
        return container;
    }

    public void initDragEvent(DragUtils.DragViewType type) {
        for (int i = 0; i < container.getChildCount(); i++) {
            //页面拖拽绑定监听器
            ViewGroup viewGroup = (ViewGroup) container.getChildAt(i);
            if (type == DragUtils.DragViewType.ITEM) {
                DragUtils.removeDragEvent(viewGroup);
            } else {
                DragUtils.setupDragEvent(this, viewGroup, DragUtils.DragViewType.PAGE, dragViewPagerListener);
            }
            //item拖拽绑定监听器
            MDA_PageListLayout layout = (MDA_PageListLayout) ((ViewGroup) viewGroup.findViewById(R.id.sv_item_page)).getChildAt(0);
            for (int j = 0; j < layout.getChildCount(); j++) {
                View view = layout.getChildAt(j);
                if (type == DragUtils.DragViewType.PAGE) {
                    DragUtils.removeDragEvent(view);
                } else {
                    DragUtils.setupDragEvent(this, view, DragUtils.DragViewType.ITEM, dragViewPagerListener);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        smoothScrollToPage(currentPage);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE://因为上层控件需要捕获触摸操作，因此只能在上层控件抛弃event，即本控件响应ACTION_MOVE事件时，进行处理
                if (touchStartX == 0 && touchStartTime == 0) {
                    touchStartX = ev.getRawX();
                    touchStartTime = System.currentTimeMillis();
                }
                break;

            case MotionEvent.ACTION_UP:
                float touchEndX = ev.getRawX();
                long touchEndTime = System.currentTimeMillis();

                if (Math.abs(touchEndX - touchStartX) > pageSwitchOffsetX &&//判断X轴偏移
                        Math.abs(touchEndX - touchStartX) / ((touchEndTime - touchStartTime) * 1.0f / 1000) > pageSwitchSpeed) {//达到切换速度
                    if (touchStartX < touchEndX) {
                        smoothScrollToPreviousPage();
                    } else {
                        smoothScrollToNextPage();
                    }
                } else {//未达到切换速度，通过滑动偏移量判断所处page
                    currentPage = (int) Math.round(getScrollX() * 1.0 / DragUtils.pageScrollWidth);
                    smoothScrollToCurrentPage();
                }
                //重置计量值
                touchStartX = 0;
                touchStartTime = 0;
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 滚动到当前页
     */
    public void smoothScrollToCurrentPage() {
//        Log.i(TAG, "smoothScrollToCurrentPage");
        smoothScrollTo(currentPage * DragUtils.pageScrollWidth, 0);
    }

    /**
     * 滚动到上一页
     */
    public void smoothScrollToPreviousPage() {
//        Log.i(TAG, "smoothScrollToPreviousPage");
        if (currentPage > 0) {//不是第一页
            currentPage--;
            smoothScrollToCurrentPage();
        }
    }

    /**
     * 滚动到下一页
     */
    public void smoothScrollToNextPage() {
//        Log.i(TAG, "smoothScrollToNextPage");
        if (currentPage < ((ViewGroup) getChildAt(0)).getChildCount() - 1) {//不是最后一页
            currentPage++;
            smoothScrollToCurrentPage();
        }
    }

    /**
     * 滚动到指定页
     *
     * @param pageIndex 指定页索引
     */
    public void smoothScrollToPage(int pageIndex) {
//        Log.i(TAG, "smoothScrollToPage");
        if (pageIndex >= 0 && pageIndex <= ((ViewGroup) getChildAt(0)).getChildCount() - 1) {//指定页索引在范围内
            currentPage = pageIndex;
            smoothScrollToCurrentPage();
        }
    }

    /**
     * 滚动到当前页
     */
    public void scrollToCurrentPage() {
//        Log.i(TAG, "scrollToCurrentPage");
        scrollTo(currentPage * DragUtils.pageScrollWidth, 0);
    }

    /**
     * 滚动到上一页
     */
    public void scrollToPreviousPage() {
//        Log.i(TAG, "scrollToPreviousPage");
        if (currentPage > 0) {//不是第一页
            currentPage--;
            scrollToCurrentPage();
        }
    }

    /**
     * 滚动到下一页
     */
    public void scrollToNextPage() {
//        Log.i(TAG, "scrollToNextPage");
        if (currentPage < ((ViewGroup) getChildAt(0)).getChildCount() - 1) {//不是最后一页
            currentPage++;
            scrollToCurrentPage();
        }
    }

    /**
     * 滚动到指定页
     *
     * @param pageIndex 指定页索引
     */
    public void scrollToPage(int pageIndex) {
//        Log.i(TAG, "scrollToPage");
        if (pageIndex >= 0 && pageIndex <= ((ViewGroup) getChildAt(0)).getChildCount() - 1) {//指定页索引在范围内
            currentPage = pageIndex;
            scrollToCurrentPage();
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public interface OnPageSelectListener {
        void onPageSelect(int currentPage);
    }
}