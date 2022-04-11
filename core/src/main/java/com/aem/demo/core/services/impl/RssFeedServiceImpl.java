package com.aem.demo.core.services.impl;

import com.aem.demo.core.config.SchedulerConfiguration;
import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component(service = RssFeedService.class, immediate = true)
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
            return Collections.emptyList();
        }

        return feed.getEntries().stream().map(NewsCard::new)
                .collect(Collectors.toList());
    }
}
