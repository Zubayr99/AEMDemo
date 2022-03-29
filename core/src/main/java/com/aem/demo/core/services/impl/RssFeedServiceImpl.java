package com.aem.demo.core.services.impl;

import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


@Component(service = RssFeedService.class)
@Slf4j
public class RssFeedServiceImpl implements RssFeedService {

    @Override
    public List<NewsCard> readFeed() {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            URL feedUrl = new URL("https://www.nasa.gov/rss/dyn/educationnews.rss");
            feed = input.build(new XmlReader(feedUrl));
        } catch (IOException | FeedException e) {
            log.error("Exception during reading news feed", e);
        }

        assert feed != null;
        return feed.getEntries().stream()
                .map(x -> new NewsCard(x.getTitle(),
                        x.getDescription().getValue(),
                        x.getUri(),
                        x.getPublishedDate(),
                        x.getEnclosures().stream()
                                .map(SyndEnclosure::getUrl)
                                .findFirst()
                                .get()))
                .collect(Collectors.toList());
    }
}
