package com.aem.demo.core.services.impl;

import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import com.aem.demo.core.utils.ImageRetrieverUtil;
import com.aem.demo.core.utils.ResolverUtil;
import com.aem.demo.core.utils.ServiceUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.Session;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.adobe.aemds.guide.utils.JcrResourceConstants.CQ_PAGE_CONTENT;
import static com.adobe.aemds.guide.utils.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;

@Slf4j
@Component(service = RssFeedService.class, immediate = true)
public class RssFeedServiceImpl implements RssFeedService {

    private final String RENDERER = "aemtraining/components/structure/home";
    private final String HOMEPAGE_TEMPLATE = "/apps/aemtraining/templates/page-home";
    private final String SITE_ROOT = "/content/aemtraining/language-masters/en";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public List<NewsCard> importData() {
        List<NewsCard> newsCardList;
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            URL feedUrl = new URL("https://www.nasa.gov/rss/dyn/educationnews.rss");
            feed = input.build(new XmlReader(feedUrl));
        } catch (IOException | FeedException e) {
            log.error("Exception during reading news feed", e);
            return Collections.emptyList();
        }
        newsCardList = feed.getEntries().stream().map(NewsCard::new).collect(Collectors.toList());
        saveData(newsCardList);
        return newsCardList;
    }

    private Page saveData(List<NewsCard> cardList) {
        Page newsPage = null;
        try (ResourceResolver resolver = ResolverUtil.newResolver(resourceResolverFactory)) {
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            String rootPath = SITE_ROOT + "/newscard-nodes";
            Page rootPage = pageManager.getPage(rootPath);
            if (rootPage == null) {
                rootPage = createPage(rootPath, pageManager);
            }
            newsPage = createChildPages(cardList, resolver, rootPage);

        } catch (Exception e) {
            log.error("\n Error while saving data: " + e.getMessage());
        }
        return newsPage;
    }


    private Page createPage(String rootPath, PageManager pageManager) {
        try {
            Page page = pageManager.getPage(rootPath);
            String pageName = "newscard-nodes";
            String pageTitle = "NewsCard Page Nodes";
            if (page == null) {
                page = pageManager.create(SITE_ROOT, pageName, HOMEPAGE_TEMPLATE, pageTitle);
                Node pageNode = page.getContentResource().adaptTo(Node.class);
                pageNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, RENDERER);
            }
            return page;
        } catch (Exception e) {
            log.error("\n Error while creating page: " + e.getMessage());
        }
        return null;
    }

    private Page createChildPages(List<NewsCard> newsCardList, ResourceResolver resolver, Page rootPage) {
        String rootPath = rootPage.getPath();
        Page page;
        try {
            Session session = resolver.adaptTo(Session.class);
            if (session != null) {
                for (NewsCard newsCard : newsCardList) {
                    PageManager pageManager = resolver.adaptTo(PageManager.class);
                    String pageName = "newspage";
                    String pageTitle = newsCard.getTopic();
                    page = pageManager.create(rootPath, pageName, HOMEPAGE_TEMPLATE, pageTitle);
                    Node pageNode = page.adaptTo(Node.class);

                    Node jcrNode;
                    if (page.hasContent()) {
                        jcrNode = page.getContentResource().adaptTo(Node.class);
                    } else {
                        jcrNode = pageNode.addNode(JCR_CONTENT, CQ_PAGE_CONTENT);
                    }
                    jcrNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, RENDERER);
                    jcrNode.setProperty("topic", newsCard.getTopic());
                    jcrNode.setProperty("article", newsCard.getArticle());
                    jcrNode.setProperty("link", newsCard.getLink());
                    jcrNode.setProperty("pubDate", String.valueOf(newsCard.getPubDate()));
                    jcrNode.setProperty("image", newsCard.getImage());
                    ImageRetrieverUtil.retrieveImages(newsCard.getImage(), resolver);
                    session.save();
                }
            }

        } catch (Exception e) {
            log.error("\n Error while creating child page: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Map<String, String>> getNewsCards() {
        String rootPath = SITE_ROOT + "/newscard-nodes";
        final List<Map<String, String>> newsCardList = new ArrayList<>();
        try (ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory)) {
            Iterator<Resource> newsCards = resourceResolver.getResource(rootPath).listChildren();
            while (newsCards.hasNext()) {
                Resource resource = newsCards.next();
                Map<String, String> newsCard = new HashMap<>();
                ValueMap property = resource.getValueMap();
                newsCard.put("topic", ServiceUtil.getProperty(property, "topic"));
                newsCard.put("article", ServiceUtil.getProperty(property, "article"));
                newsCard.put("link", ServiceUtil.getProperty(property, "link"));
                newsCard.put("pubDate", ServiceUtil.getProperty(property, "pubDate"));
                newsCard.put("image", resource.getPath() + "/photo/image");
                newsCardList.add(newsCard);
            }
        } catch (Exception e) {
            log.error("Error while retrieving the data: " + e.getMessage());
        }
        return newsCardList;
    }

}
