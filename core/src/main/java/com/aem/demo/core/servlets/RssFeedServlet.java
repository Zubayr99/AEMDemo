package com.aem.demo.core.servlets;

import com.aem.demo.core.services.RssFeedService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/apps/aemtraining/servlet/updater"
        })
public class RssFeedServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient RssFeedService rssFeedService;



    @Override
    protected void doGet(final SlingHttpServletRequest request,
                          final SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        rssFeedService.importData();
        response.getWriter().write("======RSS FEED UPDATED========");

    }
}
