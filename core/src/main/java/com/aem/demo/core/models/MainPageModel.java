package com.aem.demo.core.models;

import com.aem.demo.core.services.SearchService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, resourceType = MainPageModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MainPageModel {
    static final String RESOURCE_TYPE = "aemtraining/components/content/mainpage";
    private static final String ROOT_PATH = "/content/aemtraining/language-masters/en/newscard-nodes";
    private static final int CARDS_PERPAGE = 5;

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
    private List<Integer> pageNumbers;


    @PostConstruct
    private void init() {
        String searchText = Optional.ofNullable(servletRequest).map(SlingHttpServletRequest::getRequestParameterMap).map(map -> map.getValue("search")).map(RequestParameter::getString).orElse(null);
        String paginationNumber = Optional.ofNullable(servletRequest).map(SlingHttpServletRequest::getRequestParameterMap).map(map -> map.getValue("page")).map(RequestParameter::getString).orElse(null);
        models = searchText != null ? searchService.retrieveModels(searchText) : getAllModels();
        paginateNumbers(models);
        models = separateModels(models, paginationNumber);
    }

    private List<NewsCardModel> getAllModels() {
        List<NewsCardModel> modelsList = new ArrayList<>();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = pageManager.getPage(ROOT_PATH);
        Iterator<Page> pageIterator = page.listChildren();
        while (pageIterator.hasNext()) {
            Page currentPage = pageIterator.next();
            Resource jcrContent = currentPage.getContentResource();
            modelsList.add(jcrContent.adaptTo(NewsCardModel.class));
        }
        return modelsList;
    }

    public String getPath() {
        return resource.getPath();
    }

    private List<NewsCardModel> separateModels(List<NewsCardModel> list, String paginationNumber) {
        int pNumber = 1;
        try {
            pNumber = Integer.parseInt(paginationNumber);
        } catch (NumberFormatException e) {
            log.error("Exception occurred while parsing request parameter " + e.getMessage());
        }
        pNumber = (pNumber - 1) * CARDS_PERPAGE;
        int limit = Math.min(pNumber + CARDS_PERPAGE, list.size());
        return list.subList(pNumber, limit);
    }

    private void paginateNumbers(List<NewsCardModel> models) {
        if (models.size() > 4) {
            double listSize = models.size();
            double listPart = listSize / CARDS_PERPAGE;
            double roundPart = Math.ceil(listPart);
            pageNumbers = IntStream.rangeClosed(1, (int) (roundPart)).boxed().collect(Collectors.toList());
        } else {
            pageNumbers = Collections.singletonList(1);
        }
    }
}
