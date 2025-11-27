package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.CategoryItems;

@FunctionalInterface
public interface OnCategoryClick {
    void onClick(CategoryItems categoryItems);
}
