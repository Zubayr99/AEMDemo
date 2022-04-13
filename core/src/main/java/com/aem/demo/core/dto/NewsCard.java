package com.aem.demo.core.dto;


import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import lombok.Data;

import java.util.Date;

@Data
public class NewsCard {
    private String topic;
    private String article;
    private String link;
    private Date pubDate;
    private String image;

    public NewsCard(SyndEntry syndEntry) {
        this.topic = syndEntry.getTitle();
        this.article = syndEntry.getDescription().getValue();
        this.link = syndEntry.getLink();
        this.pubDate = syndEntry.getPublishedDate();
        this.image = syndEntry.getEnclosures().stream().map(SyndEnclosure::getUrl).findFirst().orElse("");
    }
}
