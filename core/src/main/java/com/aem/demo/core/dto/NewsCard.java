package com.aem.demo.core.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NewsCard {
    private String topic;
    private String article;
    private String link;
    private Date pubDate;
    private String image;
}
