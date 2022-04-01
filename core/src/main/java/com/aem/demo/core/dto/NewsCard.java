package com.aem.demo.core.dto;


import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NewsCard {
    private String topic;
    private SyndContent article;
    private String link;
    private Date pubDate;
    private List<SyndEnclosure> image;

    public NewsCard(SyndEntry syndEntry) {
        this.topic = syndEntry.getTitle();
        this.article = syndEntry.getDescription();
        this.link = syndEntry.getLink();
        this.pubDate = syndEntry.getPublishedDate();
        this.image = syndEntry.getEnclosures();
    }
}
