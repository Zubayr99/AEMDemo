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
import java.util.Objects;

@Slf4j
public class ImageRetrieverUtil {


    private ImageRetrieverUtil() {

    }

    public static boolean retrieveImages(String link, ResourceResolver resolver, String pageName) {
        URLConnection connection = null;
        try {
            connection = createConnection(link);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        String newFile = "/content/dam/aemtraining/" + pageName + "";
        try (InputStream inputstream = new FileInputStream(String.valueOf(tempFile))) {
            Objects.requireNonNull(resolver.adaptTo(AssetManager.class)).createAsset(newFile, inputstream, contentType, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @SneakyThrows(MalformedURLException.class)
    private static URLConnection createConnection(String url) throws IOException {
            return new URL(url).openConnection();
    }
}
