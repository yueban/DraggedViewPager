package com.bigfat.draggedviewpager.utils;

import android.view.View;

import com.bigfat.draggedviewpager.model.Page;
import com.bigfat.draggedviewpager.view.MDA_DraggedViewPager;

import java.util.Collections;
import java.util.List;

/**
 * Created by yueban on 17/7/15.
 */
public abstract class MDA_DraggedViewPagerController<T1 extends Page<T2>, T2> {
    private MDA_DraggedViewPager draggedViewPager;
    private List<T1> data;
    private int pageLayoutRes;
    private int itemLayoutRes;

    public MDA_DraggedViewPagerController(List<T1> data, int pageLayoutRes, int itemLayoutRes) {
        this.data = data;
        this.pageLayoutRes = pageLayoutRes;
        this.itemLayoutRes = itemLayoutRes;
    }

    public MDA_DraggedViewPager getDraggedViewPager() {
        return draggedViewPager;
    }

    public void setDraggedViewPager(MDA_DraggedViewPager draggedViewPager) {
        this.draggedViewPager = draggedViewPager;
    }

    public abstract void bindPageData(View pageView, int pageIndex);

    public abstract void bindItemData(View itemView, int pageIndex, int itemIndex);

    public List<T1> getData() {
        return data;
    }

    public int getPageLayoutRes() {
        return pageLayoutRes;
    }

    public int getItemLayoutRes() {
        return itemLayoutRes;
    }

    public T1 getPage(int pageIndex) {
        return data.get(pageIndex);
    }

    public T2 getItem(int pageIndex, int itemIndex) {
        return data.get(pageIndex).getData().get(itemIndex);
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
     * @param t1        添加页
     */
    public void addPage(int pageIndex, T1 t1) {
        data.add(pageIndex, t1);
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
     * @param t2        添加项
     */
    public void addItem(int pageIndex, int itemIndex, T2 t2) {
        data.get(pageIndex).getData().add(itemIndex, t2);
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
     * @param t2        更新项
     */
    public void updateItem(int pageIndex, int itemIndex, T2 t2) {
        data.get(pageIndex).getData().set(itemIndex, t2);
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
        T2 t2 = data.get(oldPageIndex).getData().remove(oldItemIndex);
        data.get(newPageIndex).getData().add(newItemIndex, t2);
        refreshDragViewPager();
    }

    private void refreshDragViewPager() {
        draggedViewPager.notifyDataSetChanged();
    }
}
