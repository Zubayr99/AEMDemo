package com.aem.demo.core.schedulers;

import com.aem.demo.core.config.SchedulerConfiguration;
import com.aem.demo.core.dto.NewsCard;
import com.aem.demo.core.services.RssFeedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.util.List;
@Slf4j
@Component(immediate = true, service = Runnable.class)
@Designate(ocd = SchedulerConfiguration.class)
public class ScheduledRssJob implements Runnable {

    private String schedulerName;

    @Reference
    private Scheduler scheduler;

    @Reference
    private RssFeedService rssFeedService;

    @Activate
    private void activate(SchedulerConfiguration configuration) {
        schedulerName = configuration.schedulerName();
        addScheduler(configuration);
    }

    @Deactivate
    protected void deactivate(SchedulerConfiguration configuration) {
        scheduler.unschedule(schedulerName);
    }


    private void addScheduler(SchedulerConfiguration configuration) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(configuration.cronExpression());
        scheduleOptions.name(schedulerName);
        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
        ScheduleOptions scheduleOptionsNow = scheduler.NOW();
        scheduler.schedule(this, scheduleOptionsNow);
    }


    @Override
    public void run() {
        List<NewsCard> cards = rssFeedService.readFeed();
        rssFeedService.saveRssFeedNodes(cards);
    }
}
