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
public class NewsCardModel {

    protected static final String RESOURCE_TYPE = "aemtraining/components/content/newscard";


    @Getter
    @ValueMapValue
    @Default(values = "topic")
    public String topic;

    @Getter
    @ValueMapValue
    @Default(values = "article")
    public String article;

    @Getter
    @ValueMapValue
    @Default(values = "link")
    public String link;

    @Getter
    @ValueMapValue
    public Date pubDate;

    @Getter
    @ValueMapValue
    @Default(values = "https://redzonekickboxing.com/wp-content/uploads/2017/04/default-image.jpg")
    public String image;


}
