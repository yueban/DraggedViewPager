package com.bigfat.draggedviewpager.utils;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

import com.bigfat.draggedviewpager.R;
import com.bigfat.draggedviewpager.view.MDA_DraggedViewPager;
import com.bigfat.draggedviewpager.view.MDA_PageListLayout;

import java.util.Collections;

public class DragUtils {
    private static final String TAG = DragUtils.class.getSimpleName();

    public static int screenWidth;
    public static int screenHeight;
    public static int pageEdgeVisibleWidth;//非当前page边缘可见宽度
    public static int pageMargin;//每一页左/右边距
    public static int pageScrollWidth;//滚动时每一页的滚动距离
    public static int pageSwapDelay = 500;//拖拽至边界时切换页面的响应延迟
    public static int pageExchangeAnimatorDelay = 300;//页面切换动画延迟（页面切换后执行动画的延迟）
    public static int itemMoveDelay = 300;//item切换响应延迟
    private static int pageScrollRunnableFlag = 0;//页面切换Runnable的标志，避免重复执行页面切换事件（0：不执行 1：下一页 2：上一页）
    private static float dragTouchX;//拖拽点在item上的X坐标
    private static float dragTouchY;//拖拽点在item上的Y坐标
    private static Handler handler = new Handler();
    private static Runnable runnable = null;
    private static boolean isAnim;//动画是否正在执行

    private DragUtils() {
    }

    /**
     * 初始化拖拽事件
     *
     * @param draggedViewPager 最外层 {@link com.bigfat.draggedviewpager.view.MDA_DraggedViewPager}
     * @param view             被初始化的View
     * @param type             拖拽View类型 {@link DragViewType}
     * @param listener         拖拽事件回调
     */
    public static void setupDragEvent(final MDA_DraggedViewPager draggedViewPager, View view, final DragViewType type, final MDA_DraggedViewPagerListener listener) {
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(final View view, final DragEvent event) {
                final ViewGroup viewGroup = (ViewGroup) view.getParent();
                final DragState dragState = (DragState) event.getLocalState();
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (view == dragState.view) {
                            view.setVisibility(View.INVISIBLE);
                            if (listener != null) {
                                listener.onDragStarted();
                            }
                        }
                        executeRunnable(null, 0);
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        switch (dragState.type) {
                            case PAGE:
                                //获取页面索引
                                final int PAGE_pageIndex = viewGroup.indexOfChild(view);
                                final int PAGE_currentPageIndex = draggedViewPager.getCurrentPage();

                                switch (getDragEventType(draggedViewPager, PAGE_currentPageIndex, PAGE_pageIndex, view, event)) {
                                    case SCROLL_PREVIOUS://滚动到上一页
                                        runnableScrollToPreviousPage(new Runnable() {
                                            @Override
                                            public void run() {
                                                //切换页
                                                swapPageViews(viewGroup, PAGE_currentPageIndex - 1, dragState, listener);
                                            }
                                        }, null);
                                        break;

                                    case SCROLL_NEXT://滚动到下一页
                                        runnableScrollToNextPage(new Runnable() {
                                            @Override
                                            public void run() {
                                                //切换页
                                                swapPageViews(viewGroup, PAGE_currentPageIndex + 1, dragState, listener);
                                            }
                                        }, null);
                                        break;

                                    case DEFAULT://不满足页面切换条件，终止正在执行的事件
                                        executeRunnable(null, 0);
                                        pageScrollRunnableFlag = 0;//重置页面切换标志
                                        break;
                                }
                                break;

                            case ITEM:
                                //获取页面索引
                                final int ITEM_pageIndex = (int) view.getTag();
                                final int ITEM_currentPageIndex = draggedViewPager.getCurrentPage();

                                switch (getDragEventType(draggedViewPager, ITEM_currentPageIndex, ITEM_pageIndex, view, event)) {
                                    case SCROLL_PREVIOUS:
                                        runnableScrollToPreviousPage(null, new Runnable() {
                                            @Override
                                            public void run() {
                                                moveItemView(dragState, ((MDA_PageListLayout) viewGroup).getDraggedViewPager().getMDA_PageListLayout(ITEM_currentPageIndex - 1), 0, listener);
                                            }
                                        });
                                        break;

                                    case SCROLL_NEXT:
                                        runnableScrollToNextPage(null, new Runnable() {
                                            @Override
                                            public void run() {
                                                moveItemView(dragState, ((MDA_PageListLayout) viewGroup).getDraggedViewPager().getMDA_PageListLayout(ITEM_currentPageIndex + 1), 0, listener);
                                            }
                                        });
                                        break;

                                    case DEFAULT:
                                        if (ITEM_pageIndex == ITEM_currentPageIndex) {//当前页item
                                            //获取index
                                            final int index = viewGroup.indexOfChild(view);

                                            //拖拽到达列表边界时，令ScrollView滚动
                                            ScrollView svPage = (ScrollView) viewGroup.getParent();

                                            ViewGroup.MarginLayoutParams itemParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
                                            int itemHeight = view.getHeight() + itemParams.topMargin + itemParams.bottomMargin;
                                            int listVisibleHeight = svPage.getHeight() - svPage.getPaddingTop() - svPage.getPaddingBottom()
                                                    - viewGroup.getPaddingTop() - viewGroup.getPaddingBottom()
                                                    - layoutParams.topMargin - layoutParams.bottomMargin;

                                            if ((index + 2) * itemHeight > listVisibleHeight + svPage.getScrollY()) {
                                                svPage.smoothScrollBy(0, itemHeight);
                                            } else if (index * itemHeight < svPage.getScrollY()) {
                                                svPage.smoothScrollBy(0, -itemHeight);
                                            }

                                            if (view != dragState.view) {//正在拖拽的item，则处理拖拽事件
                                                pageScrollRunnableFlag = 0;//重置页面切换标志

                                                //处理View移动
                                                executeRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        moveItemView(dragState, (MDA_PageListLayout) viewGroup, index, listener);
                                                    }
                                                }, itemMoveDelay);
                                            }
                                        }
                                        break;
                                }
                                break;
                        }
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        executeRunnable(new Runnable() {
                            @Override
                            public void run() {
                                dragState.view.setVisibility(View.VISIBLE);
                                if (listener != null) {
                                    listener.onDragEnded();
                                }
                                draggedViewPager.initDragEvent(DragViewType.ALL);
                            }
                        }, 0);
                        break;
                }
                return true;
            }

            /**
             * 执行延迟事务
             * @param task 要执行的事务
             * @param delay 延迟时间
             */
            private void executeRunnable(Runnable task, int delay) {
                if (isAnim) {
                    return;
                }
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                if (task != null) {
                    runnable = task;
                    handler.postDelayed(runnable, Math.max(delay, 0));
                }
            }

            /**
             * 滚动到上一页
             * @param pageSwipeRunnable 执行页面交换操作的Runnable
             * @param itemMoveRunnable 执行item移动的Runnable
             */
            private void runnableScrollToPreviousPage(final Runnable pageSwipeRunnable, final Runnable itemMoveRunnable) {
//                Log.i(TAG, "runnableScrollToPreviousPage");
                if (pageScrollRunnableFlag != 0) {//有正在执行的滚动事件
                    return;
                }
                executeRunnable(new Runnable() {
                    @Override
                    public void run() {
                        draggedViewPager.smoothScrollToPreviousPage();

                        if (pageSwipeRunnable != null) {
                            handler.postDelayed(pageSwipeRunnable, pageExchangeAnimatorDelay);
                        }

                        if (itemMoveRunnable != null) {
                            executeRunnable(itemMoveRunnable, itemMoveDelay);
                        }

                        pageScrollRunnableFlag = 0;
                    }
                }, pageSwapDelay);

                pageScrollRunnableFlag = 2;
            }

            /**
             * 滚动到下一页
             * @param pageSwipeRunnable 执行页面交换操作的Runnable
             * @param itemMoveRunnable 执行item移动的Runnable
             */
            private void runnableScrollToNextPage(final Runnable pageSwipeRunnable, final Runnable itemMoveRunnable) {
//                Log.i(TAG, "runnableScrollToNextPage");
                if (pageScrollRunnableFlag != 0) {//有正在执行的滚动事件
                    return;
                }
                executeRunnable(new Runnable() {
                    @Override
                    public void run() {
                        draggedViewPager.smoothScrollToNextPage();

                        if (pageSwipeRunnable != null) {
                            handler.postDelayed(pageSwipeRunnable, pageExchangeAnimatorDelay);
                        }

                        if (itemMoveRunnable != null) {
                            executeRunnable(itemMoveRunnable, itemMoveDelay);
                        }

                        pageScrollRunnableFlag = 0;
                    }
                }, pageSwapDelay);

                pageScrollRunnableFlag = 1;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dragTouchX = event.getX();
                        dragTouchY = event.getY();
                        break;
                }
                return false;
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.startDrag(null, new CustomDragShadowBuilder(view, dragTouchX, dragTouchY), new DragState(view, type), 0);
                draggedViewPager.initDragEvent(type);
                return true;
            }
        });
    }

    public static void removeDragEvent(View view) {
        view.setOnDragListener(null);
        view.setOnTouchListener(null);
        view.setOnLongClickListener(null);
    }

    /**
     * 移动Item
     *
     * @param dragState      被拖拽项
     * @param layoutInserted 被插入Layout
     * @param insertPosition 插入位置
     * @param listener       事件回调
     */
    private static void moveItemView(DragState dragState, MDA_PageListLayout layoutInserted, int insertPosition, MDA_DraggedViewPagerListener listener) {
        MDA_PageListLayout layoutRemoved = (MDA_PageListLayout) dragState.view.getParent();

        int oldItemIndex = dragState.index;

        //交换数据，view
        Object object = layoutRemoved.getData().get(dragState.index);
        layoutRemoved.getData().remove(dragState.index);
        layoutRemoved.notifyDataRemoved(dragState.index);

        //添加Item
        layoutInserted.getData().add(insertPosition, object);
        layoutInserted.notifyDataInserted(dragState.view, insertPosition);

        //更新被拖拽item在Layout中的索引
        layoutRemoved.setDragPosition(-1);
        layoutInserted.setDragPosition(insertPosition);
        layoutInserted.refreshVisibility();

        //设置拖拽索引
        dragState.index = insertPosition;
        //设置PageIndex
        dragState.view.setTag(layoutInserted.getPageIndex());

        //事件回调
        if (listener != null) {
            listener.onItemMoved(layoutRemoved.getPageIndex(), oldItemIndex, layoutInserted.getPageIndex(), insertPosition);
        }
    }

    /**
     * 交换页面
     *
     * @param viewGroup 页面View所在ViewGroup
     * @param index     被交换的页面索引
     * @param dragState 拖拽项（页面）
     * @param listener  事件回调
     */
    private static void swapPageViews(ViewGroup viewGroup, int index,
                                      DragState dragState, MDA_DraggedViewPagerListener listener) {
        //交换页面索引
        final MDA_PageListLayout pageListLayout = (MDA_PageListLayout) ((ViewGroup) viewGroup.getChildAt(index).findViewById(R.id.dvp_scroll_view)).getChildAt(0);
        MDA_PageListLayout dragPageListLayout = (MDA_PageListLayout) ((ViewGroup) dragState.view.findViewById(R.id.dvp_scroll_view)).getChildAt(0);
        int pageIndex = pageListLayout.getPageIndex();
        int dragPageIndex = dragPageListLayout.getPageIndex();
        pageListLayout.setPageIndex(dragPageIndex);
        dragPageListLayout.setPageIndex(pageIndex);

        //交换数据
        Collections.swap(pageListLayout.getDraggedViewPager().getData(), pageIndex, dragPageListLayout.getPageIndex());

        //获取待交换View
        final View view = viewGroup.getChildAt(index);

        //获取动画坐标
        final float viewX = view.getX();
        //交换View
        viewGroup.removeViewAt(dragState.index);
        viewGroup.addView(dragState.view, index);

        //启动动画
        DragUtils.postOnPreDraw(view, new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator
                        .ofFloat(view, View.X, viewX, view.getLeft())
                        .setDuration(DragUtils.getDuration(view));
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();
            }
        });

        //设置新的索引
        dragState.index = index;

        //事件回调
        if (listener != null) {
            listener.onPageSwapped(dragPageIndex, pageIndex);
        }
    }

    /**
     * 获取拖拽触发的执行事件类型
     *
     * @param draggedViewPager 最外层 {@link com.bigfat.draggedviewpager.view.MDA_DraggedViewPager}
     * @param currentPageIndex 当前页索引
     * @param pageIndex        响应拖拽事件item所在页索引
     * @param view             响应拖拽事件的item
     * @param event            拖拽事件
     * @return 拖拽事件类型
     */
    public static DragEventType getDragEventType(MDA_DraggedViewPager draggedViewPager, int currentPageIndex, int pageIndex, View view, DragEvent event) {
        if (currentPageIndex > 0//在有上一页的前提下
                && (pageIndex < currentPageIndex//触摸至上一页
                || (pageIndex == currentPageIndex && event.getX() < view.getWidth() / 8))) {//或至触摸至当前页左边界，则切换到下一页
            return DragEventType.SCROLL_PREVIOUS;
        } else if (currentPageIndex < draggedViewPager.getContainer().getChildCount() - 1 &&//在有下一页的前提下
                (pageIndex > currentPageIndex//触摸至下一页
                        || (pageIndex == currentPageIndex && event.getX() > view.getWidth() / 8 * 7))) {//或至触摸至当前页右边界，则切换到下一页
            return DragEventType.SCROLL_NEXT;
        }
        return DragEventType.DEFAULT;
    }

    public static int getDuration(View view) {
        return view.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public static void postOnPreDraw(View view, final Runnable runnable) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }
                runnable.run();
                return true;
            }
        });
    }

    public enum DragViewType {
        ALL,//所有类型
        PAGE,//页面
        ITEM,//item
    }

    /**
     * 拖拽触发事件类型
     */
    public enum DragEventType {
        SCROLL_PREVIOUS,//滚到上一页
        SCROLL_NEXT,//滚到下一页
        DEFAULT,//其他
    }

    private static class DragState {
        public View view;
        public int index;
        public DragViewType type;

        private DragState(View view, DragViewType type) {
            this.view = view;
            this.type = type;
            index = ((ViewGroup) view.getParent()).indexOfChild(view);
        }
    }
}