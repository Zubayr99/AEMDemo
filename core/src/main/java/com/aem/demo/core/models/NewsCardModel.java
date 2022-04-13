package com.aem.demo.core.models;

import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Date;

@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = NewsCardModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class NewsCardModel {

    static final String RESOURCE_TYPE = "aemtraining/components/content/newscard";
    private static final String DEFAULT_IMAGE = "https://redzonekickboxing.com/wp-content/uploads/2017/04/default-image.jpg";


    @ValueMapValue
    @Default(values = "topic")
    public String topic;

    @ValueMapValue
    @Default(values = "article")
    public String article;

    @ValueMapValue
    @Default(values = "link")
    public String link;

    @ValueMapValue
    public Date pubDate;

    @ValueMapValue
    @Default(values = DEFAULT_IMAGE)
    public String image;

}
