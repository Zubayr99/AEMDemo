package com.aem.demo.core.services;

import com.aem.demo.core.dto.NewsCard;

import java.util.List;
import java.util.Map;

public interface RssFeedService {
    List<NewsCard> importData();

    List<Map<String, String>> getNewsCards();
}
