package dk.trustworks.cronmanager.jobs;

import dk.trustworks.cronmanager.client.RestClient;
import org.jooby.quartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by hans on 07/06/15.
 */
public class BudgetCleanupJob {

    private static boolean hasRun = false;

    @Scheduled("5m")
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("BudgetCleanupJob.execute");
        if (hasRun) return;
        RestClient restClient = new RestClient();

        for (int i = 0; i < 12; i++) {
            System.out.println("i = " + i);
            restClient.updateBudgetByMonthAndYear(i, 2014);
        }

        for (int i = 0; i < 12; i++) {
            System.out.println("i = " + i);
            restClient.updateBudgetByMonthAndYear(i, 2015);
        }

        for (int i = 0; i < 4; i++) {
            System.out.println("i = " + i);
            restClient.updateBudgetByMonthAndYear(i, 2016);
        }

        //restClient.updateBudgetByMonthAndYear(0, 2016);
        hasRun = true;
    }

}
