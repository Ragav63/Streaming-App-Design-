package com.example.streamingapp;

public class CategoryHomeItems {
    String categoryHomeTitle;
    int categoryHomeImg;

    public CategoryHomeItems(String categoryHomeTitle, int categoryHomeImg) {
        this.categoryHomeTitle = categoryHomeTitle;
        this.categoryHomeImg = categoryHomeImg;
    }

    public String getCategoryHomeTitle() {
        return categoryHomeTitle;
    }

    public void setCategoryHomeTitle(String categoryHomeTitle) {
        this.categoryHomeTitle = categoryHomeTitle;
    }

    public int getCategoryHomeImg() {
        return categoryHomeImg;
    }

    public void setCategoryHomeImg(int categoryHomeImg) {
        this.categoryHomeImg = categoryHomeImg;
    }
}
