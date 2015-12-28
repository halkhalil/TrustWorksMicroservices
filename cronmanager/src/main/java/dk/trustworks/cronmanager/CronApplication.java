package dk.trustworks.cronmanager;

import dk.trustworks.cronmanager.jobs.BudgetCleanupJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by hans on 07/06/15.
 */
public class CronApplication {

    public static void main(String[] args) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        JobDetail job = newJob(BudgetCleanupJob.class)
                .withIdentity("budgetCleanupJob1", "monthlyJobs")
                .build();
        CronTrigger trigger = newTrigger()
                .withIdentity("trigger1", "monthlyJobs")
                .withSchedule(cronSchedule("0/60 * * * * ?"))
                .build();
        sched.scheduleJob(job, trigger);
        sched.start();
    }

}
