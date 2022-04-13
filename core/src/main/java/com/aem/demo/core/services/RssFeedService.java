package com.aem.demo.core.services;

import com.aem.demo.core.dto.NewsCard;

import java.util.List;

public interface RssFeedService {
    List<NewsCard> readFeed();

    String saveRssFeedNodes(List<NewsCard> cardList);
}
