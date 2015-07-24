package com.bigfat.draggedviewpager.model;

import java.util.ArrayList;

/**
 * Created by yueban on 13/7/15.
 */
public abstract class Page<T> {
    private ArrayList<T> data;

    public Page() {
    }

    public Page( ArrayList<T> data) {
        this.data = data;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }
}