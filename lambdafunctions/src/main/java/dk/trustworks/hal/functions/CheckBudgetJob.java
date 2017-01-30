package dk.trustworks.hal.functions;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Field;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.client.timemanager.RestClient;
import dk.trustworks.framework.model.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.*;


public class CheckBudgetJob {

    private final RestClient restClient = new RestClient();
    private SlackWebApiClient halWebApiClient = SlackClientFactory.createWebApiClient(System.getProperty("HAL_SLACK_TOKEN"));

    public void execute() {
        System.out.println("CheckBudgetJob.execute");
        System.out.println("System.getenv(\"HAL_SLACK_TOKEN\") = " + System.getenv("HAL_SLACK_TOKEN"));
        System.out.println("System.getProperty(\"HAL_SLACK_TOKEN\") = " + System.getProperty("HAL_SLACK_TOKEN"));

        LocalDate dateNextMonth = LocalDate.now().plusMonths(2);
        System.out.println("dateNextMonth = " + dateNextMonth);

        List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByMonthAndYear(dateNextMonth.getMonthOfYear() - 1, dateNextMonth.getYear());
        System.out.println("budgets.get(0).month = " + budgets.get(0).month);
        dateNextMonth = dateNextMonth.plusMonths(1);
        budgets.addAll(restClient.getBudgetsByMonthAndYear(dateNextMonth.getMonthOfYear() - 1, dateNextMonth.getYear()));
        System.out.println("budgets.size() = " + budgets.size());
        List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
        System.out.println("projects.size() = " + projects.size());

        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = new HashMap<>();
        for (Project project : projects) {
            for (Task task : project.tasks) {
                task.project = project;
                for (TaskWorkerConstraint taskWorkerConstraint : task.taskworkerconstraints) {
                    taskWorkerConstraint.task = task;
                    taskWorkerConstraintMap.put(taskWorkerConstraint.useruuid+taskWorkerConstraint.taskuuid, taskWorkerConstraint);
                }
            }
        }

        Date startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1).toDate();
        System.out.println("startDate = " + startDate);
        Date endDate = LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(1).toDate();
        System.out.println("endDate = " + endDate);

        int businessDaysInNextMonth = getWorkingDaysBetweenTwoDates(startDate, endDate);
        System.out.println("businessDaysInMonth = " + businessDaysInNextMonth);

        int businessDaysInNextNextMonth = getWorkingDaysBetweenTwoDates(LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(1).toDate(), LocalDate.now().plusMonths(3).withDayOfMonth(1).minusDays(1).toDate());
        System.out.println("businessDaysInMonth = " + businessDaysInNextNextMonth);

        for (User user : restClient.getUsers()) {
            System.out.println("user.slackusername = " + user.slackusername);
            if(!user.username.equals("hans.lassen")) continue;
            allbegray.slack.type.User slackUser = getSlackUser(user);

            String message = "*Here is a quick summary of "+LocalDate.now().plusMonths(1).monthOfYear().getAsText()+"*\n\n" +
                    "According to my calculations there is "+businessDaysInNextMonth+" work days in "+LocalDate.now().plusMonths(1).monthOfYear().getAsText()+" " +
                    "and "+businessDaysInNextNextMonth+" in "+LocalDate.now().plusMonths(2).monthOfYear().getAsText()+".\n\n";

            message += "You have the following tasks assigned in "+LocalDate.now().plusMonths(1).monthOfYear().getAsText()+":\n";

            double totalBudgetMonthOne = 0.0;
            double totalBudgetMonthTwo = 0.0;
            Map<String, Attachment> attachments = new HashMap<>();
            for (TaskWorkerConstraintBudget budget : budgets) {
                System.out.println("budget.taskuuid = " + budget.taskuuid);
                System.out.println("budget.useruuid = " + budget.useruuid);
                TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(budget.useruuid+budget.taskuuid);
                if(!budget.useruuid.equals(user.uuid)) continue;
                System.out.println("taskWorkerConstraint = " + taskWorkerConstraint);
                Task task = taskWorkerConstraint.task;
                System.out.println("task = " + task);
                Project project = task.project;
                double budgetHours = (budget.budget / taskWorkerConstraint.price);

                if(budget.month == (DateTime.now().plusMonths(1).getMonthOfYear()-1)) {
                    totalBudgetMonthOne += budgetHours;
                } else {
                    totalBudgetMonthTwo += budgetHours;
                }

                Attachment attachment;
                if(!attachments.containsKey(task.uuid)) { // Hvis dette er den første gang en task optræder
                    attachment = new Attachment();
                    attachment.setTitle(project.name);
                    attachment.setText(task.name);
                    attachment.setColor("#fbb14d");
                    attachments.put(task.uuid, attachment);
                    if(budget.month > (DateTime.now().plusMonths(1).getMonthOfYear()-1)) { // Hvis dette er første gang en task optrædder og det er i måned 2
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1-1).monthOfYear().getAsText(), "0 hours", true));
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1).monthOfYear().getAsText(), (Math.round(budgetHours*100.0)/100.0)+" hours", true));
                    } else { // Hvis dette er første gang en task optrædder og det er i måned 1
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1).monthOfYear().getAsText(), (Math.round(budgetHours*100.0)/100.0)+" hours", true));
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1+1).monthOfYear().getAsText(), "0 hours", true));
                    }

                } else { // Hvis det er anden gang en tank optræder
                    attachment = attachments.get(task.uuid);
                    if(attachment.getFields().size() == 2) { // Slet et eventuelt dummy budget
                        attachment.getFields().remove(1);
                    }
                    // Tilføj budgettet
                    attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1).monthOfYear().getAsText(), (Math.round(budgetHours*100.0)/100.0)+" hours", true));
                }
            }


            ChatPostMessageMethod textMessage = new ChatPostMessageMethod(user.slackusername, message);
            textMessage.setAs_user(true);
            textMessage.setAttachments(new ArrayList<>(attachments.values()));
            System.out.println("Sending message");
            halWebApiClient.postMessage(textMessage);

            List<Capacity> userCapacities = restClient.getUserCapacities(user.uuid, LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).plusMonths(2));
            System.out.println("userCapacities.get(0).capacity = " + userCapacities.get(0).capacity);
            System.out.println("userCapacities.get(1).capacity = " + userCapacities.get(1).capacity);
            System.out.println("businessDaysInNextMonth = " + businessDaysInNextMonth);
            System.out.println("totalBudgetMonthOne = " + totalBudgetMonthOne);


            long allocationPercentMonthOne = Math.round((totalBudgetMonthOne / ((userCapacities.get(0).capacity / 5) * businessDaysInNextMonth)) * 100);
            long allocationPercentMonthTwo = Math.round((totalBudgetMonthTwo / ((userCapacities.get(1).capacity / 5) * businessDaysInNextNextMonth)) * 100);
            String concludingMessage = "";

            concludingMessage += "If this seems ok, do nothing. If this seems wrong, please contact your project leads and tell them to fix it!";

            textMessage = new ChatPostMessageMethod(user.slackusername, concludingMessage);
            textMessage.setAs_user(true);
            System.out.println("Sending concluding message");
            halWebApiClient.postMessage(textMessage);

            ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "User "+user.username+" has "+allocationPercentMonthOne+"% and "+allocationPercentMonthTwo+"% allocation.");
            textMessage2.setAs_user(true);
            System.out.println("Sending message");
            halWebApiClient.postMessage(textMessage2);
/*
            if(allocationPercentMonthOne < 75.0 || allocationPercentMonthOne > 100.0 || allocationPercentMonthTwo < 75.0 || allocationPercentMonthTwo > 100.0) {
                ChatPostMessageMethod textMessage3 = new ChatPostMessageMethod("@tobias_kjoelsen", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage3.setAs_user(true);
                System.out.println("Sending message");
                //halWebApiClient.postMessage(textMessage3);

                ChatPostMessageMethod textMessage4 = new ChatPostMessageMethod("@peter", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage4.setAs_user(true);
                System.out.println("Sending message");
                //halWebApiClient.postMessage(textMessage4);

                ChatPostMessageMethod textMessage5 = new ChatPostMessageMethod("@thomasgammelvind", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage5.setAs_user(true);
                System.out.println("Sending message");
                //halWebApiClient.postMessage(textMessage5);
            }
            */
        }

    }

    private allbegray.slack.type.User getSlackUser(User user) {
        int levenshsteinScore = 100;
        allbegray.slack.type.User slackUser = null;
        for (allbegray.slack.type.User slackUserIteration : halWebApiClient.getUserList()) {
            int levenshteinDistance = StringUtils.getLevenshteinDistance(user.firstname + " " + user.lastname, slackUserIteration.getProfile().getReal_name());
            System.out.println("levenshteinDistance = " + levenshteinDistance);
            System.out.println("slackUserIteration.getProfile().getReal_name() = " + slackUserIteration.getProfile().getReal_name());
            if(levenshteinDistance < levenshsteinScore) {
                levenshsteinScore = levenshteinDistance;
                slackUser = slackUserIteration;
            }
        }
        System.out.println("Identified slackUser.getName() = " + slackUser.getName());
        return slackUser;
    }

    public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        System.out.println("CheckTimeRegistrationJob.getWorkingDaysBetweenTwoDates");
        System.out.println("startDate = [" + startDate + "], endDate = [" + endDate + "]");
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
