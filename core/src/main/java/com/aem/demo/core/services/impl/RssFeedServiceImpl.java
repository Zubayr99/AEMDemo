package com.aem.demo.core.services.impl;

import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import com.aem.demo.core.utils.ImageRetrieverUtil;
import com.aem.demo.core.utils.ResolverUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.Session;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.adobe.aemds.guide.utils.JcrResourceConstants.CQ_PAGE_CONTENT;
import static com.adobe.aemds.guide.utils.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

@Slf4j
@Component(service = RssFeedService.class, immediate = true)
public class RssFeedServiceImpl implements RssFeedService {

    private final String RENDERER = "aemtraining/components/structure/home";
    private final String TEMPLATE = "/apps/aemtraining/templates/page-home";
    private final String NODE_PATH = "/content/aemtraining/language-masters/en";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public List<NewsCard> readFeed() {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            URL feedUrl = new URL("https://www.nasa.gov/rss/dyn/educationnews.rss");
            feed = input.build(new XmlReader(feedUrl));
        } catch (IOException | FeedException e) {
            log.error("Exception during reading news feed", e);
            return Collections.emptyList();
        }
        return feed.getEntries().stream().map(NewsCard::new).collect(Collectors.toList());
    }

    @Override
    public String saveRssFeedNodes(List<NewsCard> cardList) {
        String nodeCreated = StringUtils.EMPTY;
        try (ResourceResolver resolver = ResolverUtil.newResolver(resourceResolverFactory)) {
            Session session = resolver.adaptTo(Session.class);

            if (session.nodeExists(NODE_PATH + "/newscard-nodes")) {
                nodeCreated = createPage(cardList, resolver);
            } else {
                addParentNode(session);
                nodeCreated = createPage(cardList, resolver);

            }

        } catch (Exception e) {
            log.error("\n Error while creating node - {} ", e.getMessage());
        }
        return nodeCreated;
    }


    private String addParentNode(Session session) {
        try {
            if (session.nodeExists(NODE_PATH)) {
                Node parentNodePath = session.getNode(NODE_PATH);
                Node parentNode = parentNodePath.addNode("newscard-nodes", NT_UNSTRUCTURED);
                session.save();
                return parentNode.getName();
            }
        } catch (Exception e) {
            log.error("\n Error while creating Parent node ");
        }
        return null;
    }

    private String createPage(List<NewsCard> newsCardList, ResourceResolver resolver) {

        Page prodPage;
        try {
            Session session = resolver.adaptTo(Session.class);
            if (session != null) {
                for (NewsCard newsCard : newsCardList) {
                    PageManager pageManager = resolver.adaptTo(PageManager.class);
                    String pageName = "newspage";
                    String pageTitle = "NewsCard Page";
                    prodPage = pageManager.create(NODE_PATH + "/newscard-nodes", pageName, TEMPLATE, pageTitle);
                    Node pageNode = prodPage.adaptTo(Node.class);

                    Node jcrNode;
                    if (prodPage.hasContent()) {
                        jcrNode = prodPage.getContentResource().adaptTo(Node.class);
                    } else {
                        jcrNode = pageNode.addNode(JCR_CONTENT, CQ_PAGE_CONTENT);
                    }
                    jcrNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, RENDERER);
                    jcrNode.setProperty("topic", newsCard.getTopic());
                    jcrNode.setProperty("article", newsCard.getArticle());
                    jcrNode.setProperty("link", newsCard.getLink());
                    jcrNode.setProperty("pubDate", String.valueOf(newsCard.getPubDate()));
                    jcrNode.setProperty("image", newsCard.getImage());
                    ImageRetrieverUtil.retrieveImages(jcrNode, newsCard.getLink(), resolver);
                    session.save();
                }
            }

        } catch (Exception e) {
            log.error("\n Error while creating NewsCard page! ");
        }
        return null;
    }

}
