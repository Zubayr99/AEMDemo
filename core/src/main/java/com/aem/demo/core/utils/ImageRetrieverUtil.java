package com.aem.demo.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.adobe.aemds.guide.utils.JcrResourceConstants.NT_SLING_FOLDER;
import static com.day.cq.commons.jcr.JcrConstants.*;
import static com.day.cq.dam.commons.handler.StandardImageHandler.JPEG_MIMETYPE;

@Slf4j
public class ImageRetrieverUtil {


    public ImageRetrieverUtil() {

    }

    public static boolean retrieveImages(Node node, String link, ResourceResolver resolver) {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            String redirect = connection.getHeaderField("Location");
            if (redirect != null) {
                connection = new URL(redirect).openConnection();
            }
            InputStream stream = connection.getInputStream();
            Session session = resolver.adaptTo(Session.class);
            ValueFactory valueFactory = session.getValueFactory();
            Binary imageBinary = valueFactory.createBinary(stream);
            Node photo = node.addNode("photo", NT_SLING_FOLDER);
            Node file = photo.addNode("image", NT_FILE);
            Node content = file.addNode(JCR_CONTENT, NT_RESOURCE);
            content.setProperty(JCR_MIMETYPE, JPEG_MIMETYPE);
            content.setProperty(JCR_DATA, imageBinary);

        } catch (Exception e) {
            log.info("\n ERROR while retrieving the image - {} ", e.getMessage());
        }

        return false;
    }


}
