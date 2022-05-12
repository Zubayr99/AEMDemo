package com.aem.demo.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

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

    public String getPath() {
        return resource.getPath().replace("/jcr:content","");
    }
}
