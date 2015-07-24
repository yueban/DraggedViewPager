package com.bigfat.sample;

import com.bigfat.draggedviewpager.model.Page;

import java.util.ArrayList;

/**
 * Created by yueban on 24/7/15.
 */
public class Section<T> extends Page<T> {
    private String title;

    public Section(ArrayList<T> data, String title) {
        super(data);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
