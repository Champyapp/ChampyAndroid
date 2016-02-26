package com.example.ivan.champy_v2.data;


import com.example.ivan.champy_v2.model.Article;

import java.util.List;

public interface DataProvider {
    List<Article> getArticles();
}
