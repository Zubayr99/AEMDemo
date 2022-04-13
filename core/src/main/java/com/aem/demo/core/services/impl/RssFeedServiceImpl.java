package com.aem.demo.core.services.impl;

import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import com.aem.demo.core.utils.ResolverUtil;
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

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component(service = RssFeedService.class, immediate = true)
public class RssFeedServiceImpl implements RssFeedService {

    private final String NEWSCARDNODE_TYPE= "nt:unstructured";
    private final String NODE_PATH = "/content/aemtraining/us/en/us-newscard";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

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

    @Override
    public String saveRssFeedNodes(List<NewsCard> cardList) {
        String nodeCreated= StringUtils.EMPTY;
        try {
            ResourceResolver resolver = ResolverUtil.newResolver(resourceResolverFactory);
            Session session = resolver.adaptTo(Session.class);

            if (session.nodeExists(NODE_PATH)){
                nodeCreated = addNewsCardNode(session, cardList);
            } else {
                addParentNode(session);
                nodeCreated = addNewsCardNode(session, cardList);
            }

        } catch (Exception e) {
            log.error("\n Error while creating node - {} ",e.getMessage());
        }
        return nodeCreated;
    }

    private int count = 0;

    private String addNewsCardNode(Session session, List<NewsCard> list){

        try {
            for (NewsCard elm:list) {
                count++;
                String nodeName= count + " newscard_node";
                Node node = session.getNode(NODE_PATH);
                Node cardNode = node.addNode(nodeName, NEWSCARDNODE_TYPE);
                cardNode.setProperty("topic", elm.getTopic());
                cardNode.setProperty("article", elm.getArticle());
                cardNode.setProperty("link", elm.getLink());
                cardNode.setProperty("pubDate", String.valueOf(elm.getPubDate()));
                cardNode.setProperty("image", elm.getImage());
                saveImage(cardNode, elm.getImage());
                session.save();
            }


        } catch (Exception e) {
            log.error("\n Error while creating NewsCard node ");
        }
        return null;
    }

    private String addParentNode(Session session){
        try {
            if(session.nodeExists("/content/aemtraining/us/en")){
                Node gParentNode=session.getNode("/content/aemtraining/us/en");
                Node parentNode=gParentNode.addNode("us-newscard",NEWSCARDNODE_TYPE);
                session.save();
                return parentNode.getName();
            }
        }catch (Exception e){
            log.error("\n Error while creating Parent node ");
        }
        return null;
    }

    private boolean saveImage(Node node, String link) {
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);

            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            Session session=resourceResolver.adaptTo(Session.class);
            ValueFactory valueFactory=session.getValueFactory();
            Binary imageBinary = valueFactory.createBinary(stream);
            Node photo=node.addNode("photo","sling:Folder");
            Node file=photo.addNode("image","nt:file");
            Node content = file.addNode("jcr:content", "nt:resource");
            content.setProperty("jcr:mimeType", "image/jpeg");
            content.setProperty("jcr:data", imageBinary);

        } catch (Exception e) {
            log.info("\n ERROR while saving images - {} ",e.getMessage());
        }
        return false;
    }

}
