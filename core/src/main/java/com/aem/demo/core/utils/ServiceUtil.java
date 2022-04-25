package com.aem.demo.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

public class ServiceUtil {

    private ServiceUtil() {

    }

    public static String getProperty(ValueMap valueMap, String property) {
        if (StringUtils.isNotBlank(valueMap.get(property, String.class))) {
            return valueMap.get(property, String.class);
        }
        return "NA";
    }
}
