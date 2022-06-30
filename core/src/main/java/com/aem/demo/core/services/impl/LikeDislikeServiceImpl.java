package com.aem.demo.core.services.impl;

import com.aem.demo.core.services.LikeDislikeService;
import com.aem.demo.core.utils.ResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Slf4j
@Component(service = LikeDislikeService.class, immediate = true)
public class LikeDislikeServiceImpl implements LikeDislikeService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public int updateLikeDislike(String property, String rootPath) {
        int incrementProperty;
        int result = 0;
        try (ResourceResolver resolver = ResolverUtil.newResolver(resourceResolverFactory)) {
            Resource resource = resolver.getResource(rootPath);
            ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
            incrementProperty = properties.get(property, 0);
            properties.put(property, incrementProperty + 1);
            result = properties.get(property, 0);
            resolver.commit();
        } catch (LoginException | PersistenceException e) {
            log.error("Exception occurred updating likes and dislikes " + e.getMessage());
        }
        return result;
    }
}
