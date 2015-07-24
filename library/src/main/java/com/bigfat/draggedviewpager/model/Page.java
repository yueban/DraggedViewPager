package com.bigfat.draggedviewpager.model;

import java.util.List;

/**
 * Created by yueban on 13/7/15.
 */
public interface Page<T> {
    List<T> getData();

    void setData(List<T> data);
}