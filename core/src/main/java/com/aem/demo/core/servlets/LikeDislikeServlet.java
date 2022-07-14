package com.aem.demo.core.servlets;

import com.aem.demo.core.services.LikeDislikeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Slf4j
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Like and Dislike Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=" + "/services/reaction"
        })
public class LikeDislikeServlet extends SlingAllMethodsServlet {
    private static final String LIKE = "like";
    private static final String DISLIKE = "dislike";

    @Reference
    LikeDislikeService service;

    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        String reaction = request.getParameter("reaction");
        String path = request.getParameter("path");
        JSONObject responseData = new JSONObject();
        try {
            if (reaction.equals(LIKE)) {
                int value = service.updateLikeDislike(LIKE, path);
                responseData.put(LIKE, value);
            } else if (reaction.equals(DISLIKE)) {
                int value = service.updateLikeDislike(DISLIKE, path);
                responseData.put(DISLIKE, value);
            }
        } catch (JSONException e) {
            log.error(e.getMessage());
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(responseData.toString());
    }

}
