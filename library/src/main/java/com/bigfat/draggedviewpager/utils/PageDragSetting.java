package com.bigfat.draggedviewpager.utils;

/**
 * Created by yueban on 21/9/15.
 */
public interface PageDragSetting {
    boolean canBeDragged(int pageIndex);

    boolean canBeSwiped(int pageIndex);
}
