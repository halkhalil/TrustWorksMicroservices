package dk.trustworks.hal.functions;

import dk.trustworks.client.timemanager.RestClient;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.model.TaskWorkerConstraintBudget;
import dk.trustworks.framework.model.Work;
import org.joda.time.LocalDate;

import java.util.*;

/**
 * Created by hans on 13/02/2017.
 */
public class BudgetCleanupJob {

    private final int month;
    private final int year;

    public BudgetCleanupJob() {
        month = LocalDate.now().minusMonths(1).getMonthOfYear()-1;
        year = LocalDate.now().getYear();
    }

    public BudgetCleanupJob(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public void execute() {
        System.out.println("ProjectBudgetHandler.budgetcleanup");

        RestClient restClient = new RestClient();
        List<TaskWorkerConstraintBudget> workBudgets = new ArrayList<>();
        Map<String, TaskWorkerConstraintBudget> workBudgetMap = new HashMap<>();
        for (Work work : restClient.getRegisteredWorkByMonth(year, month)) {
            TaskWorkerConstraint taskWorkerConstraint = restClient.getTaskWorkerConstraint(work.taskuuid, work.useruuid);
            if (!workBudgetMap.containsKey(taskWorkerConstraint.uuid)) {
                workBudgetMap.put(taskWorkerConstraint.uuid, new TaskWorkerConstraintBudget(UUID.randomUUID().toString(), 0.0, month, year, work.useruuid, work.taskuuid));
            }
            TaskWorkerConstraintBudget currentBudget = workBudgetMap.get(taskWorkerConstraint.uuid);
            if (taskWorkerConstraint.uuid == null) System.err.println("LOG00180: " + taskWorkerConstraint);
            currentBudget.budget = (currentBudget.budget + (work.workduration * taskWorkerConstraint.price));
            currentBudget.taskuuid = taskWorkerConstraint.taskuuid; //.setTaskWorkerConstraintUUID(taskWorkerConstraint.getUUID());
            currentBudget.useruuid = taskWorkerConstraint.useruuid; //.setTaskWorkerConstraintUUID(taskWorkerConstraint.getUUID());
            //currentBudget.setTaskWorkerConstraint(taskWorkerConstraint);
        }
        workBudgets.addAll(workBudgetMap.values());

        for (TaskWorkerConstraintBudget workBudget : workBudgets) {
            System.out.println("newBudgets: " + workBudget.useruuid + ", " + workBudget.taskuuid + " ("+workBudget.year + "/" + (workBudget.month+1) + "): " + workBudget.budget);
        }

        List<TaskWorkerConstraintBudget> actualBudgets = restClient.getBudgetsByMonthAndYear(month+1, year); // find de faktiske budgetter
        for (TaskWorkerConstraintBudget actualBudget : actualBudgets) {
            System.out.println("actualBudget = " + actualBudget);
        }


        List<TaskWorkerConstraintBudget> newBudgets = new ArrayList<>(); // nye budgetter
        List<TaskWorkerConstraintBudget> newRemoveList = new ArrayList<>(); // budgetter der skal fjernes
        for (TaskWorkerConstraintBudget newBudget : workBudgets) { // Gå igennem de nye budgetter der er dannet ud fra work items.
            List<TaskWorkerConstraintBudget> actualRemoveList = new ArrayList<>();

            //System.out.println("actualBudgets = " + actualBudgets.size());
            System.out.println("-----");
            System.out.println();
            for (TaskWorkerConstraintBudget actualBudget : actualBudgets) {
                if(actualBudget.taskuuid.equals(newBudget.taskuuid) && actualBudget.useruuid.equals(newBudget.useruuid)) {
                    System.out.println(actualBudget.useruuid + " == "+ newBudget.useruuid);
                    System.out.println(actualBudget.taskuuid + " == "+ newBudget.taskuuid);
                    System.out.println("MATCH!!!");
                    System.out.println("actualBudget = " + actualBudget);
                    System.out.println("newBudget = " + newBudget);
                    System.out.println();
                }
                if(actualBudget.taskuuid.equals(newBudget.taskuuid) &&
                        actualBudget.useruuid.equals(newBudget.useruuid) &&
                        actualBudget.year == newBudget.year &&
                        actualBudget.month == newBudget.month) {
                    System.out.println("actualBudget.budget = " + actualBudget.budget + " --> " + newBudget.budget);
                }
            }


            actualBudgets.stream().filter(actualBudget -> actualBudget.taskuuid.equals(newBudget.taskuuid) &&
                    actualBudget.useruuid.equals(newBudget.useruuid)).forEach(actualBudget -> {
                //System.out.println("actualBudget.budget = " + actualBudget.budget + " --> " + newBudget.budget);
                actualBudget.budget = newBudget.budget;

                newBudgets.add(actualBudget);
                actualRemoveList.add(actualBudget);
                newRemoveList.add(newBudget);
            });
            actualBudgets.removeAll(actualRemoveList);
        }
        workBudgets.removeAll(newRemoveList);
        newBudgets.addAll(workBudgets);

        for (TaskWorkerConstraintBudget actualBudget : actualBudgets) {
            actualBudget.budget = 0.0;
            newBudgets.add(actualBudget);
        }

        newBudgets.forEach(restClient::postTaskBudget);
    }

    public static void main(String[] args) {
        //new BudgetCleanupJob(6, 2015).execute();

        LocalDate localDate = new LocalDate(2015, 7, 1);
        LocalDate now = LocalDate.now().minusMonths(1);
        while(localDate.isBefore(now)) {
            new BudgetCleanupJob(localDate.getMonthOfYear()-1, localDate.getYear()).execute();
            localDate = localDate.plusMonths(1);
        }

    }
}
