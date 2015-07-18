package com.bigfat.draggedviewpager.utils;

import android.view.View;

import com.bigfat.draggedviewpager.model.Page;
import com.bigfat.draggedviewpager.view.MDA_DraggedViewPager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yueban on 17/7/15.
 */
public abstract class MDA_DraggedViewPagerController<T> {
    private MDA_DraggedViewPager draggedViewPager;
    private ArrayList<Page<T>> data;
    private int itemLayoutRes;

    public MDA_DraggedViewPagerController(ArrayList<Page<T>> data, int itemLayoutRes) {
        this.data = data;
        this.itemLayoutRes = itemLayoutRes;
    }

    public MDA_DraggedViewPager getDraggedViewPager() {
        return draggedViewPager;
    }

    public void setDraggedViewPager(MDA_DraggedViewPager draggedViewPager) {
        this.draggedViewPager = draggedViewPager;
    }

    public abstract void bindItemData(View itemView, T t);

    public ArrayList<Page<T>> getData() {
        return data;
    }

    public int getItemLayoutRes() {
        return itemLayoutRes;
    }

    /**
     * 交换页面
     *
     * @param firstIndex  第一页索引
     * @param secondIndex 第二页索引
     */
    public void swapPages(int firstIndex, int secondIndex) {
        Collections.swap(data, firstIndex, secondIndex);
        refreshDragViewPager();
    }

    /**
     * 添加页
     *
     * @param pageIndex 添加页索引
     * @param page      添加页
     */
    public void addPage(int pageIndex, Page<T> page) {
        data.add(pageIndex, page);
        refreshDragViewPager();
    }

    /**
     * 删除页
     *
     * @param pageIndex 删除页索引
     */
    public void removePage(int pageIndex) {
        data.remove(pageIndex);
        refreshDragViewPager();
    }

    /**
     * 添加item
     *
     * @param pageIndex item添加页索引
     * @param itemIndex item添加位置索引
     * @param item      添加项
     */
    public void addItem(int pageIndex, int itemIndex, T item) {
        data.get(pageIndex).getData().add(itemIndex, item);
        refreshDragViewPager();
    }

    /**
     * 删除item
     *
     * @param pageIndex item所在页索引
     * @param itemIndex item位置索引
     */
    public void removeItem(int pageIndex, int itemIndex) {
        data.get(pageIndex).getData().remove(itemIndex);
        refreshDragViewPager();
    }

    /**
     * 更新item
     *
     * @param pageIndex item所在页位置
     * @param itemIndex item位置索引
     * @param item      更新项
     */
    public void updateItem(int pageIndex, int itemIndex, T item) {
        data.get(pageIndex).getData().set(itemIndex, item);
        refreshDragViewPager();
    }

    /**
     * 移动item
     *
     * @param oldPageIndex item所在页索引
     * @param oldItemIndex item位置索引
     * @param newPageIndex item移动页索引
     * @param newItemIndex item移动位置索引
     */
    public void moveItem(int oldPageIndex, int oldItemIndex, int newPageIndex, int newItemIndex) {
        T item = data.get(oldPageIndex).getData().remove(oldItemIndex);
        data.get(newPageIndex).getData().add(newItemIndex, item);
        refreshDragViewPager();
    }

    private void refreshDragViewPager() {
        draggedViewPager.initView(this);
    }
}
