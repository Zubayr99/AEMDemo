package com.aem.demo.core.models;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Slf4j
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
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

    private int likes;

    private int dislikes;

    @Self
    private SlingHttpServletRequest servletRequest;

    @SlingObject
    private ResourceResolver resourceResolver;


    public String getPath() {
        return resource.getPath().replace("/jcr:content", "");
    }

    public String getFullPath() {
        return resource.getPath();
    }



    @PostConstruct
    private void init() {
        String rootPath = resource.getPath();
        String dislikeParam = servletRequest.getParameter("dislike");
        String likeParam = servletRequest.getParameter("like");
        if (likeParam != null) {
            likes = incrementLikeDislike(likeParam, rootPath);
        } else if (dislikeParam != null) {
            dislikes = incrementLikeDislike(dislikeParam, rootPath);
        }

    }


    private int incrementLikeDislike(String property, String rootPath) {
        int incrementProperty = 0;
        resource = resourceResolver.getResource(rootPath);
        ModifiableValueMap properties = resource != null ? resource.adaptTo(ModifiableValueMap.class) : null;
        if (properties != null) {
            incrementProperty = properties.get(property, 0);
            properties.put(property, incrementProperty + 1);
        }
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            log.error("Exception occurred while incrementing like or dislike " + e.getMessage());
        }
        return incrementProperty;
    }

}
