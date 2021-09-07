package com.linhx.sso.configs;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.services.RequestAccessTokenService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Scheduler
 *
 * @author linhx
 * @since 12/11/2020
 */
@Configuration
public class SchedulerConfig {
    private final RequestAccessTokenService requestAccessTokenService;
    private final ApplicationContext applicationContext;

    public SchedulerConfig(RequestAccessTokenService requestAccessTokenService, ApplicationContext applicationContext) {
        this.requestAccessTokenService = requestAccessTokenService;
        this.applicationContext = applicationContext;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteInvalidRequestAccessToken() throws BaseException {
        this.requestAccessTokenService.deleteInvalidRequestsAccessToken();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
        quartzScheduler.setApplicationContext(this.applicationContext);
        quartzScheduler.setSchedulerFactory(new StdSchedulerFactory());
        var jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        quartzScheduler.setJobFactory(jobFactory);
        return quartzScheduler;
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        var scheduler = schedulerFactoryBean().getScheduler();
        scheduler.start();
        return scheduler;
    }
}

class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory
        implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}