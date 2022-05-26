package com.aem.demo.core.services.impl;

import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import com.aem.demo.core.utils.ImageRetrieverUtil;
import com.aem.demo.core.utils.ResolverUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
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

    private final String RESOURCE_TYPE = "aemtraining/components/structure/singlepage";
    private final String GRIDPAGE_TEMPLATE = "/apps/aemtraining/templates/page-home";
    private final String SITE_ROOT = "/content/aemtraining/language-masters/en";
    private final String RSSFEED_LINK = "https://www.nasa.gov/rss/dyn/educationnews.rss";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public List<NewsCard> importData() {
        List<NewsCard> newsCardList;
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            URL feedUrl = new URL(RSSFEED_LINK);
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
            Page rootPage = Optional.ofNullable(pageManager).map(pm -> pm.getPage(rootPath)).orElse(null);
            if (rootPage == null) {
                rootPage = createPage(rootPath, pageManager);
            }
            newsPage = saveChildPages(cardList, resolver, rootPage);

        } catch (LoginException e) {
            log.error("\n Error while saving data: " + e.getMessage());
        }
        return newsPage;
    }


    private Page createPage(String rootPath, PageManager pageManager) {
        Page page = null;
        try {
            page = pageManager.getPage(rootPath);
            String pageName = "newscard-nodes";
            String pageTitle = "NewsCard Page Nodes";
            if (page == null) {
                page = pageManager.create(SITE_ROOT, pageName, GRIDPAGE_TEMPLATE, pageTitle);
                Node pageNode = page.getContentResource().adaptTo(Node.class);
                pageNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, RESOURCE_TYPE);
            }
        } catch (WCMException | RepositoryException e) {
            log.error("\n Error while creating page: " + e.getMessage());
        }
        return page;
    }


    private Page saveChildPages(List<NewsCard> newsCardList, ResourceResolver resolver, Page rootPage) {
        int importedItemsCount = 1;
        String rootPath = rootPage.getPath();
        Session session = resolver.adaptTo(Session.class);
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return null;
        }
        Set<String> names = getSavedNodeNames(rootPath, pageManager);
        if (session == null) {
            return null;
        }
        for (NewsCard newsCard : newsCardList) {
            if (importedItemsCount > 3) {
                break;
            }
            String pageName = StringUtils.substringAfterLast(newsCard.getLink(), "/");
            if (names.contains(pageName)) {
                continue;
            }
            String pageTitle = newsCard.getTopic();
            Page page;
            try {
                page = pageManager.create(rootPath, pageName, GRIDPAGE_TEMPLATE, pageTitle);
            } catch (WCMException e) {
                continue;
            }
            Node pageNode = page.adaptTo(Node.class);
            if (pageNode == null) {
                continue;
            }
            createChildPage(newsCardList, newsCard, page, pageNode, session);
            ImageRetrieverUtil.retrieveImages(newsCard.getImage(), resolver, pageName);
            importedItemsCount++;
        }
        return null;
    }

    private void createChildPage(List<NewsCard> newsCardList, NewsCard newsCard, Page page, Node pageNode, Session session) {
        try {
            Node jcrNode;
            if (page.hasContent()) {
                jcrNode = page.getContentResource().adaptTo(Node.class);
            } else {
                jcrNode = pageNode.addNode(JCR_CONTENT, CQ_PAGE_CONTENT);
            }
            Objects.requireNonNull(jcrNode).setProperty(SLING_RESOURCE_TYPE_PROPERTY, RESOURCE_TYPE);
            jcrNode.setProperty("topic", newsCard.getTopic());
            jcrNode.setProperty("article", newsCard.getArticle());
            jcrNode.setProperty("link", newsCard.getLink());
            jcrNode.setProperty("pubDate", String.valueOf(newsCard.getPubDate()));
            jcrNode.setProperty("image", newsCard.getImage());
            if (newsCardList.get(newsCardList.size() - 1).equals(newsCard)) {
                session.save();
            }
        } catch (RepositoryException e) {
            log.error("Exception occurred while creating child page: " + e.getMessage());
        }
    }

    private Set<String> getSavedNodeNames(String rootPath, PageManager pageManager) {
        Page page = pageManager.getPage(rootPath);
        Iterator<Page> pageIterator = page.listChildren();
        Set<String> names = new HashSet<>();
        while (pageIterator.hasNext()) {
            names.add(pageIterator.next().getName());
        }
        return names;
    }

}
