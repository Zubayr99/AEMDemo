package com.aem.demo.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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

    @ValueMapValue
    private int like;

    @ValueMapValue
    private int dislike;

    private List<String> tagsList;


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
        tagsList = new ArrayList<>();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag[] pageTags = tagManager.getTags(resource);
        for (Tag tag : pageTags) {
            tagsList.add(tag.getTitle());
        }
    }
}
