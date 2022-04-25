package com.aem.demo.core.utils;

import com.day.cq.dam.api.AssetManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
public class ImageRetrieverUtil {


    private ImageRetrieverUtil() {

    }

    public static boolean retrieveImages(String link, ResourceResolver resolver) {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            String redirect = connection.getHeaderField("Location");
            if (redirect != null) {
                connection = new URL(redirect).openConnection();
            }
            String contentType = connection.getContentType();
            Path tempFile = Files.createTempFile(null, null);
            InputStream stream = connection.getInputStream();
            Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            String nodeName = StringUtils.substringBefore(link, "?");
            nodeName = StringUtils.substringAfterLast(nodeName, "/");
            String newFile = "/content/dam/aemtraining/" + nodeName + "";
            try (InputStream inputstream = new FileInputStream(String.valueOf(tempFile))) {
                resolver.adaptTo(AssetManager.class).createAsset(newFile, inputstream, contentType, true);
            }

        } catch (Exception e) {
            log.info("\n ERROR while retrieving the image - {} ", e.getMessage());
        }

        return false;
    }


}
