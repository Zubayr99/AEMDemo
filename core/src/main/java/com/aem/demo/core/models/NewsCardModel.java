package com.aem.demo.core.models;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;

@Slf4j
@Model(adaptables = Resource.class,
        resourceType = NewsCardModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class NewsCardModel {

    static final String RESOURCE_TYPE = "aemtraining/components/content/newscard";
    private static final String DEFAULT_IMAGE = "https://redzonekickboxing.com/wp-content/uploads/2017/04/default-image.jpg";

    @SlingObject
    private Resource resource;

    @ValueMapValue
    @Default(values = "topic")
    private String topic;

    @ValueMapValue
    @Default(values = "article")
    private String article;

    @ValueMapValue
    @Default(values = "link")
    private String link;

    @ValueMapValue
    private String pubDate;

    @ValueMapValue
    @Default(values = DEFAULT_IMAGE)
    private String image;

//    private int likes;

    @Inject
    private SlingHttpServletRequest servletRequest;

    @SlingObject
    private ResourceResolver resourceResolver;


    public String getPath() {
        return resource.getPath().replace("/jcr:content", "");
    }

    public String getFullPath() {
        return resource.getPath();
    }


    public int  getLikes() {
        int likes = 0;

        String rootPath = resource.getPath();

        resource = resourceResolver.getResource(rootPath);
        ModifiableValueMap properties = resource != null ? resource.adaptTo(ModifiableValueMap.class) : null;

        if (properties != null) {
            likes = resource.adaptTo(ValueMap.class).get("likes", 0);
            properties.put("likes", likes + 1);
        }
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            log.info(e.getMessage());
        }
        return likes;
    }

    public int  getDisLikes() {
        String dislikeParameter = servletRequest.getParameter("dislikeId");
        int dislikes = 0;

        String rootPath = resource.getPath();

        resource = resourceResolver.getResource(rootPath);
        ModifiableValueMap properties = resource != null ? resource.adaptTo(ModifiableValueMap.class) : null;

        if (properties != null && dislikeParameter.equals("false")) {
            dislikes = resource.adaptTo(ValueMap.class).get("dislikes", 0);
            properties.put("dislikes", dislikes + 1);
        }
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            log.info(e.getMessage());
        }
        return dislikes;
    }
}
