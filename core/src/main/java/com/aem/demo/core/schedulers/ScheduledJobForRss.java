package com.aem.demo.core.schedulers;

import com.aem.demo.core.config.SchedulerConfiguration;
import com.aem.demo.core.services.RssFeedService;
import com.aem.demo.core.dto.NewsCard;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(immediate = true, service = Runnable.class)
@Slf4j
public class ScheduledJobForRss implements Runnable {

    private int schedulerId;

    @Reference
    private Scheduler scheduler;

    @Reference
    private RssFeedService rssFeedService;

    @Activate
    private void activate(SchedulerConfiguration configuration) {
        schedulerId = configuration.schedulerName().hashCode();
        addScheduler(configuration);
    }

    @Deactivate
    protected void deactivate(SchedulerConfiguration configuration) {
        removeScheduler();
    }


    protected void removeScheduler() {
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    private void addScheduler(SchedulerConfiguration configuration) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(configuration.cronExpression());
        scheduleOptions.name(String.valueOf(schedulerId));
        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
        ScheduleOptions scheduleOptionsNow = scheduler.NOW();
        scheduler.schedule(this, scheduleOptionsNow);
    }


    @Override
    public void run() {
        List<NewsCard> cards = rssFeedService.readFeed();
        log.info("Run method from scheduler is working!");
    }
}
