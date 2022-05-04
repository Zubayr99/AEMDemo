package com.aem.demo.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MainPageModel {
    static final String RESOURCE_TYPE = "aemtraining/components/content/mainpage";

    @Inject
    NewsCardModel newsCardModel;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Getter
    private List<NewsCardModel> models;

    @PostConstruct
    private void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return;
        }
        models = new ArrayList<>();
        Page page = pageManager.getPage("/content/aemtraining/language-masters/en/newscard-nodes");
        Iterator<Page> pageIterator = page.listChildren();
        while (pageIterator.hasNext()) {
            Page currentPage = pageIterator.next();
            Resource jcrContent = currentPage.getContentResource();
            models.add(jcrContent.adaptTo(NewsCardModel.class));
        }
    }

    public String getPath() {
        return newsCardModel.getPath();
    }

}
