package com.aem.demo.core.models;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = CardFooterModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class CardFooterModel {
    static final String RESOURCE_TYPE = "aemtraining/components/content/footer";
    private static final String DEFAULT_LOGO = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbEcTC5pyFCmCwnGeRAsxAMr8bjIhTUZvAsgA3tsDtbPvLUxFKc-1H9xnfUmoAcVIudQ&usqp=CAU";
    private static final String DEFAULT_COMMON_INFO = "Lorem ipsum dolor sit amet, consectateur adispicing elit. Fusce euismod convallis velit, eu auctor lacu...";

    @ValueMapValue
    @Default(values = DEFAULT_LOGO)
    private String logo;

    @ValueMapValue
    @Default(values = DEFAULT_COMMON_INFO)
    private String projectInfo;
}
