package com.aem.demo.core.services;

import com.aem.demo.core.models.NewsCardModel;

import java.util.List;

public interface SearchService {
    List<NewsCardModel> retrieveModels(String searchText);
}
