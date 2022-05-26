package com.aem.demo.core.models;


import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MapHelper {

    @Inject
    private SlingHttpServletRequest servletRequest;

    @Inject
    private int currentPage;


    public Map<String, String> getQueryParams() {
        Map<String, String> map = new HashMap<>();

        servletRequest.getRequestParameterMap();

        RequestParameterMap requestParameterMap = servletRequest.getRequestParameterMap();

        for (Map.Entry<String, RequestParameter[]> entry: requestParameterMap.entrySet()) {

            RequestParameter[] parameters = entry.getValue();

            if (parameters == null) {
                continue;
            }

            if (entry.getValue().length > 1) {
                RequestParameter requestParameter = entry.getValue()[0];
                map.put(entry.getKey(), requestParameter.getString());
            }
        }

        map.put("page", String.valueOf(currentPage));

        return map;
    }

}
