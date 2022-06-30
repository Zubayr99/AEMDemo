package com.aem.demo.core.utils;

import com.day.cq.dam.api.AssetManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
public class ImageRetrieverUtil {


    private ImageRetrieverUtil() {

    }

    public static boolean retrieveImages(String link, ResourceResolver resolver, String pageName) {
        URLConnection connection = null;
        try {
            connection = createConnection(link);
        } catch (IOException e) {
            log.error("Exception occurred while creating connection: " + e.getMessage());
        }
        if (connection == null) {
            return false;
        }
        String contentType = connection.getContentType();
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, null);
            InputStream stream = connection.getInputStream();
            Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Exception occurred while saving data to a temporary file: " + e.getMessage());
        }

        String newFile = "/content/dam/aemtraining/" + pageName + "";
        try (InputStream inputstream = new FileInputStream(String.valueOf(tempFile))) {
            Optional.ofNullable(resolver.adaptTo(AssetManager.class)).map(am -> am.createAsset(newFile, inputstream, contentType, true));
        } catch (IOException e) {
            log.error("Exception occurred while creating the asset: " + e.getMessage());
        }

        return false;
    }

    @SneakyThrows(MalformedURLException.class)
    private static URLConnection createConnection(String url) throws IOException {
        return new URL(url).openConnection();
    }
}
