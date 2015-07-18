package com.bigfat.draggedviewpager.utils;

public interface MDA_DraggedViewPagerListener {
    /**
     * 拖拽开始
     */
    void onDragStarted();

    /**
     * 拖拽结束
     */
    void onDragEnded();

    /**
     * 拖拽页面交换
     *
     * @param firstPageIndex  第一页索引
     * @param secondPageIndex 第二页索引
     */
    void onPageSwapped(int firstPageIndex, int secondPageIndex);

    /**
     * 拖拽item移动
     *
     * @param oldPageIndex item原所在页索引
     * @param oldItemIndex item原位置索引
     * @param newPageIndex item新所在页索引
     * @param newItemIndex item新位置索引
     */
    void onItemMoved(int oldPageIndex, int oldItemIndex, int newPageIndex, int newItemIndex);
}