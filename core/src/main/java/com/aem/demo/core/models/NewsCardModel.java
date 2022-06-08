package com.aem.demo.core.models;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
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

    private Integer likes = 0;

    private Integer dislikes = 0;

    @Self
    private SlingHttpServletRequest servletRequest;

    @SlingObject
    private ResourceResolver resourceResolver;

    public String getPath() {
        return resource.getPath().replace("/jcr:content", "");
    }

    @PostConstruct
    private void init() {
        String dislikeParam = servletRequest.getParameter("false");
        String likeParam = servletRequest.getParameter("true");
        if (likeParam != null) {
            likes = incrementLikeDislike(likeParam);
        } else if (dislikeParam != null) {
            dislikes = incrementLikeDislike(dislikeParam);
        }
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        likes = valueMap.get("like", Integer.class);
        dislikes = valueMap.get("dislike", Integer.class);
    }

    private int incrementLikeDislike(String property) {
        int incrementProperty;
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        incrementProperty = properties.get(property, 0);
        properties.put(property, incrementProperty + 1);
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            log.error("Exception occurred while incrementing like or dislike " + e.getMessage());
        }
        return incrementProperty;
    }

}
