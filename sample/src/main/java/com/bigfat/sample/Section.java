package com.bigfat.sample;

import com.bigfat.draggedviewpager.model.Page;

import java.util.List;

/**
 * Created by yueban on 24/7/15.
 */
public class Section implements Page<Item> {
    private String title;
    private List<Item> items;

    public Section(String title, List<Item> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<Item> getData() {
        return items;
    }

    @Override
    public void setData(List<Item> data) {
        this.items = data;
    }
}
