package com.bigfat.sample;

/**
 * Created by yueban on 17/7/15.
 */
public class Item {
    private int imgRes;
    private String text;

    public Item() {
    }

    public Item(int imgRes, String text) {
        this.imgRes = imgRes;
        this.text = text;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Item{" +
                "imgRes=" + imgRes +
                ", text='" + text + '\'' +
                '}';
    }
}
