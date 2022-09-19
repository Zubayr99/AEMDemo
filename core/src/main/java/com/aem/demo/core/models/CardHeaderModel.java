package com.aem.demo.core.models;

import com.exadel.aem.toolkit.api.annotations.main.AemComponent;
import com.exadel.aem.toolkit.api.annotations.main.Dialog;
import com.exadel.aem.toolkit.api.annotations.widgets.DialogField;
import com.exadel.aem.toolkit.api.annotations.widgets.rte.RichTextEditor;
import com.exadel.aem.toolkit.api.annotations.widgets.rte.RteFeatures;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = CardHeaderModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@AemComponent(path = "content/header", title = "Header Component title")
@Dialog(extraClientlibs = "eak.checklist")
public class CardHeaderModel {
    static final String RESOURCE_TYPE = "aemtraining/components/content/header";
    private static final String DEFAULT_LOGO = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbEcTC5pyFCmCwnGeRAsxAMr8bjIhTUZvAsgA3tsDtbPvLUxFKc-1H9xnfUmoAcVIudQ&usqp=CAU";
    private static final String GRIDPAGE_PATH = "/content/aemtraining/language-masters/en/mainpage";


    @ValueMapValue
    @Default(values = DEFAULT_LOGO)
    private String logo;

    @ValueMapValue
    @DialogField(label = "test test")
    @Default(values = "Default text is here..")
    @RichTextEditor(
            features = {
                    RteFeatures.UNDO_UNDO,
                    RteFeatures.UNDO_REDO,
                    RteFeatures.SEPARATOR,
                    RteFeatures.Popovers.EDIT_ALL,
                    RteFeatures.SEPARATOR,
                    RteFeatures.Popovers.FORMAT_ALL,
                    RteFeatures.Popovers.STYLES,
                    RteFeatures.Popovers.JUSTIFY_ALL,
                    RteFeatures.Popovers.LISTS_ALL,
                    RteFeatures.SEPARATOR,
                    RteFeatures.MISCTOOLS_SPECIALCHARS,
                    RteFeatures.SEPARATOR,
                    RteFeatures.Popovers.PARAFORMAT
            }
    )
    private String title;

    public String getPath() {
        return GRIDPAGE_PATH;
    }
}
