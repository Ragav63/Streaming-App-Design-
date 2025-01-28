package com.example.streamingapp;

public class PickVideoTypeRecItem {


    private int itemImg;
    private String itemTitle;

    public PickVideoTypeRecItem(int itemImg, String itemTitle) {
        this.itemImg = itemImg;
        this.itemTitle = itemTitle;
    }

    public int getItemImg() {
        return itemImg;
    }

    public void setItemImg(int itemImg) {
        this.itemImg = itemImg;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }
}
