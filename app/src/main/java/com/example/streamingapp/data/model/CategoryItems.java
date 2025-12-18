package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@AllArgsConstructor     // full constructor
@NoArgsConstructor
public class CategoryItems {
    String categoryTitle;
    int categoryImg;
}
