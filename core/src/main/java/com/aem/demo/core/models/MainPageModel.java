package com.aem.demo.core.models;

import com.aem.demo.core.services.SearchService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = MainPageModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MainPageModel {
    static final String RESOURCE_TYPE = "aemtraining/components/content/mainpage";
    private static final String ROOT_PATH = "/content/aemtraining/language-masters/en/newscard-nodes";

    @Inject
    private SlingHttpServletRequest servletRequest;

    @OSGiService
    SearchService searchService;

    @Inject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Getter
    private List<NewsCardModel> models;

    @Getter
    List<Integer> pageNumbers;

    List<NewsCardModel> holdModels;

    @PostConstruct
    private void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return;
        }
        models = new ArrayList<>();
        holdModels = new ArrayList<>();
        Page page = pageManager.getPage(ROOT_PATH);
        Iterator<Page> pageIterator = page.listChildren();
        while (pageIterator.hasNext()) {
            Page currentPage = pageIterator.next();
            Resource jcrContent = currentPage.getContentResource();
            holdModels.add(jcrContent.adaptTo(NewsCardModel.class));
        }

        double listSize = holdModels.size();
        double listPart = listSize / 5;
        double roundPart = Math.round(listPart);

        if (roundPart != listPart) {
            roundPart++;
        }

        pageNumbers = IntStream.range(1, (int) (roundPart + 1)).boxed().collect(Collectors.toList());

        String searchText = servletRequest.getParameter("search");
        String paginationNumber = servletRequest.getParameter("page");

        if (searchText != null) {
            models = searchService.retrieveModels(searchText);
        } else if (paginationNumber != null) {
            models = separateModels(holdModels, Integer.parseInt(paginationNumber));
        } else {
            models = holdModels.subList(0, 5);
        }
    }

    public String getPath() {
        return resource.getPath();
    }

    private List<NewsCardModel> separateModels(List<NewsCardModel> list, int paginationNumber) {
        int limit = Math.min(paginationNumber * 5, list.size());
        List<NewsCardModel> separatedPart = new ArrayList<>();
        for (int i = (paginationNumber - 1) * 5; i < limit; i++) {
            separatedPart.add(list.get(i));
        }
        return separatedPart;
    }

}
