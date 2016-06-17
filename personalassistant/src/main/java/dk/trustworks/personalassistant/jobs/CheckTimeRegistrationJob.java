package dk.trustworks.personalassistant.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.*;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.client.RestClient;
import dk.trustworks.personalassistant.dto.timemanager.*;
import dk.trustworks.personalassistant.dto.timemanager.User;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.jooby.quartz.Scheduled;

import java.util.*;

/**
 * Created by hans on 15/06/16.
 */
public class CheckTimeRegistrationJob {

    private final RestClient restClient = new RestClient();
    private SlackWebApiClient halWebApiClient = SlackClientFactory.createWebApiClient(System.getenv("HAL_SLACK_TOKEN"));

    @Scheduled("0 0 12 * * ?")
    public void checkTimeRegistration() {
        System.out.println("CheckTimeRegistrationJob.checkTimeRegistration");
        DateTime dateTime = DateTime.now();
        if(dateTime.getDayOfWeek() > 5) return; // do not check in weekends
        System.out.println("This is not in the weekend");

        dateTime = (dateTime.getDayOfWeek() == 1) ? dateTime.minusDays(3) : dateTime.minusDays(2);
        System.out.println("dateTime = " + dateTime);

        List<Work> allWork = new ArrayList<>();
        allWork.addAll(restClient.getRegisteredWorkByYearMonthDay(dateTime.getYear(), (dateTime.getMonthOfYear() - 1), dateTime.getDayOfMonth()));
        dateTime = dateTime.plusDays(1);
        allWork.addAll(restClient.getRegisteredWorkByYearMonthDay(dateTime.getYear(), (dateTime.getMonthOfYear() - 1), dateTime.getDayOfMonth()));

        System.out.println("workByYearMonthDay.size() = " + allWork.size());

        for (User user : restClient.getUsers()) {
            if(user.getAllocation() == 0) continue;
            System.out.println("checking user = " + user);
            boolean hasWork = false;
            for (Work work : allWork) {
                if(work.getUserUUID().equals(user.getUUID())) hasWork = true;
            }
            System.out.println("hasWork = " + hasWork);
            if(!hasWork) {
                allbegray.slack.type.User slackUser = getSlackUser(user);
                ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), user.getFirstname()+", you haven´t registered your hours, "+user.getFirstname()+"...");
                textMessage.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage);

                ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "Notification sent to: "+user.getUsername()+" at "+slackUser.getName());
                textMessage2.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage2);
            }
        }
    }

    //0 15 10 ? * 6L
    @Scheduled("0 0 10 ? * 6L")
    public void checkBudget() {
        System.out.println("CheckBudgetJob.checkTimeRegistration");
        DateTime dateNextMonth = DateTime.now().plusMonths(1);
        System.out.println("dateNextMonth = " + dateNextMonth);

        List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByMonthAndYear(dateNextMonth.getMonthOfYear() - 1, dateNextMonth.getYear());
        System.out.println("budgets.size() = " + budgets.size());
        List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
        System.out.println("projects.size() = " + projects.size());
        List<Work> thisMonthWork = restClient.getRegisteredWorkByMonth(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear() - 1);
        System.out.println("thisMonthWork.size() = " + thisMonthWork.size());

        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = new HashMap<>();
        for (Project project : projects) {
            for (Task task : project.getTasks()) {
                task.setProject(project);
                for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) {
                    taskWorkerConstraint.setTask(task);
                    taskWorkerConstraintMap.put(taskWorkerConstraint.getUUID(), taskWorkerConstraint);
                }
            }
        }

        Date startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1).toDate();
        System.out.println("startDate = " + startDate);
        Date endDate = LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(1).toDate();
        System.out.println("endDate = " + endDate);

        int businessDaysInMonth = getWorkingDaysBetweenTwoDates(startDate, endDate);
        System.out.println("businessDaysInMonth = " + businessDaysInMonth);

        for (User user : restClient.getUsers()) {
            //if(!user.getUsername().equals("hans.lassen")) continue;
            allbegray.slack.type.User slackUser = getSlackUser(user);

            String message = "*Here is a quick summery of your next month*\n\n" +
                    "According to my calculations there is "+businessDaysInMonth+" work days in the month.\n\n";

            message += "You have the following budgets assigned for next month:\n";

            double totalBudget = 0.0;
            List<Attachment> attachments = new ArrayList<>();
            for (TaskWorkerConstraintBudget budget : budgets) {
                //String projectMessage = "";
                TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(budget.getTaskWorkerConstraintUUID());
                if(!taskWorkerConstraint.getUserUUID().equals(user.getUUID())) continue;
                System.out.println("taskWorkerConstraint = " + taskWorkerConstraint);
                Task task = taskWorkerConstraint.getTask();
                System.out.println("task = " + task);
                Project project = task.getProject();
                System.out.println("project = " + project);
                double budgetHours = (budget.getBudget() / taskWorkerConstraint.getPrice());
                //projectMessage += project.getName()+" / "+task.getName()+" / "+budgetHours+"\n\n";
                totalBudget += budgetHours;

                Attachment attachment = new Attachment();
                attachment.setTitle(project.getName());
                attachment.setText(task.getName());
                attachment.addField(new Field("Budget next month", (Math.round(budgetHours*100.0)/100.0)+"", true));

                double workHours = 0.0;
                for (Work work : thisMonthWork) {
                    if(work.getUserUUID().equals(user.getUUID()) && work.getTaskUUID().equals(task.getUUID())) {
                        workHours += work.getWorkDuration();
                    }
                }

                attachment.addField(new Field("Hours worked this month", workHours+"", true));

                attachments.add(attachment);
            }

            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), message);
            textMessage.setAs_user(true);
            textMessage.setAttachments(attachments);
            System.out.println("Sending message");
            halWebApiClient.postMessage(textMessage);

            long allocationPercent = Math.round((totalBudget / ((user.getAllocation() / 5) * businessDaysInMonth)) * 100);
            String concludingMessage = "This means you have a *"+allocationPercent+"%* allocation this coming month\n\n";

            concludingMessage += "If this seems ok, do nothing. If this seems wrong, please contact your project leads and tell them to fix it!";

            textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), concludingMessage);
            textMessage.setAs_user(true);
            System.out.println("Sending concluding message");
            halWebApiClient.postMessage(textMessage);

            ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "User "+user.getUsername()+" has "+allocationPercent+"% allocation");
            textMessage2.setAs_user(true);
            System.out.println("Sending message");
            halWebApiClient.postMessage(textMessage2);

        }

    }

    private allbegray.slack.type.User getSlackUser(User user) {
        int levenshsteinScore = 100;
        allbegray.slack.type.User slackUser = null;
        for (allbegray.slack.type.User slackUserIteration : halWebApiClient.getUserList()) {
            int levenshteinDistance = StringUtils.getLevenshteinDistance(user.getFirstname() + " " + user.getLastname(), slackUserIteration.getProfile().getReal_name());
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

    public static void main(String[] args) {
        Date startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1).toDate();
        System.out.println("startDate = " + startDate.getTime());
        Date endDate = LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(1).toDate();
        System.out.println("endDate = " + endDate.getTime());

        int businessDaysInMonth = getWorkingDaysBetweenTwoDates(startDate, endDate);
        System.out.println("days = " + businessDaysInMonth);
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
