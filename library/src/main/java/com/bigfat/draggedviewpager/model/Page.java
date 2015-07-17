package com.bigfat.draggedviewpager.model;

import java.util.ArrayList;

/**
 * Created by yueban on 13/7/15.
 */
public class Page<T> {
    private String title;
    private ArrayList<T> data;

    public Page() {
    }

    public Page(String title, ArrayList<T> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Page{" +
                "title='" + title + '\'' +
                ", data=" + data +
                '}';
    }
}
