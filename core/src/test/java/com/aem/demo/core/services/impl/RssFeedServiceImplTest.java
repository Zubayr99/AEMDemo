package com.aem.demo.core.services.impl;


import junit.framework.TestCase;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class RssFeedServiceImplTest extends TestCase {

    private RssFeedServiceImpl rssFeedService = new RssFeedServiceImpl();

    private TestLogger logger = TestLoggerFactory.getTestLogger(rssFeedService.getClass());

    public void testReadFeed() {
        rssFeedService.importData().forEach(System.out::println);
    }
}