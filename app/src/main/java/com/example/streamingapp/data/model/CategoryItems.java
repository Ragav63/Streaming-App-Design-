package com.example.streamingapp.data.model;

public class CategoryItems {
    String categoryTitle;
    int categoryImg;

    public CategoryItems(String categoryTitle, int categoryImg) {
        this.categoryTitle = categoryTitle;
        this.categoryImg = categoryImg;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public int getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(int categoryImg) {
        this.categoryImg = categoryImg;
    }
}
