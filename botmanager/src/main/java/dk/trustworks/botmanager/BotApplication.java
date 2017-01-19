package dk.trustworks.botmanager;

import dk.trustworks.botmanager.jobs.CheckBudgetJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 06/01/2017.
 */
@Configuration
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "dk.trustworks.botmanager"})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean(){
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(CheckBudgetJob.class);
        factory.setGroup("mygroup");
        factory.setName("myjob");
        return factory;
    }

    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean2(){
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(CheckBudgetJob.class);
        factory.setGroup("mygroup");
        factory.setName("myjob2");
        return factory;
    }

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean(){
        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(jobDetailFactoryBean().getObject());
        stFactory.setStartDelay(3000);
        stFactory.setName("mytrigger");
        stFactory.setGroup("mygroup");
        stFactory.setCronExpression("0 0 10 20W * ?");
        return stFactory;
    }

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean2(){
        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(jobDetailFactoryBean2().getObject());
        stFactory.setStartDelay(3000);
        stFactory.setName("mytrigger2");
        stFactory.setGroup("mygroup");
        stFactory.setCronExpression("0 0 10 10W * ?");
        return stFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(cronTriggerFactoryBean().getObject());
        scheduler.setTriggers(cronTriggerFactoryBean2().getObject());
        return scheduler;
    }
}
