package com.aem.demo.core.services.impl;

import com.aem.demo.core.models.NewsCardModel;
import com.aem.demo.core.services.SearchService;
import com.aem.demo.core.utils.ResolverUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.util.*;

import static com.adobe.aemds.guide.utils.JcrResourceConstants.CQ_PAGE;

@Slf4j
@Component(service = SearchService.class, immediate = true)
public class SearchServiceImpl implements SearchService {

    private static final String ROOT_PATH = "/content/aemtraining/language-masters";


    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private Map<String, String> createTextSearchQuery(String searchText) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", ROOT_PATH);
        queryMap.put("type", CQ_PAGE);
        queryMap.put("fulltext", searchText);
        return queryMap;
    }

    @Override
    public List<NewsCardModel> retrieveModels(String searchText) {
        List<NewsCardModel> models = new ArrayList<>();
        try (ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory)) {
            final Session session = resourceResolver.adaptTo(Session.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(createTextSearchQuery(searchText)), session);
            SearchResult result = query.getResult();
            for (Iterator<Resource> it = result.getResources(); it.hasNext(); ) {
                Resource res = it.next();
                NewsCardModel model = res.adaptTo(Page.class).getContentResource().adaptTo(NewsCardModel.class);
                models.add(model);
            }
        } catch (LoginException e) {
            log.error("Exception occurred while retrieving pages " + e.getMessage());
        }
        return models;
    }
}
