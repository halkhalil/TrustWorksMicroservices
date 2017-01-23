package dk.trustworks.botmanager.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Field;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.botmanager.network.timemanager.RestClient;
import dk.trustworks.framework.model.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by hans on 15/06/16.
 */
@Component
public class CheckTimeRegistrationJob {

    private final RestClient restClient = new RestClient();
    private SlackWebApiClient halWebApiClient = SlackClientFactory.createWebApiClient(System.getProperty("HAL_SLACK_TOKEN"));

    @Scheduled(cron = "0 0 12 * * ?")
    //@Scheduled(fixedRate = 2000)
    public void checkTimeRegistration() {
        System.out.println("CheckTimeRegistrationJob.checkTimeRegistration");
        DateTime dateTime = DateTime.now();
        if(dateTime.getDayOfWeek() > 5) return; // do not check in weekends
        System.out.println("This is not in the weekend");

        if(dateTime.getDayOfWeek() == 1) {
            dateTime = dateTime.minusDays(3);
        } else if (dateTime.getDayOfWeek() == 2) {
            dateTime = dateTime.minusDays(4);
        } else {
            dateTime = dateTime.minusDays(2);
        }
        System.out.println("dateTime = " + dateTime);

        List<Work> allWork = new ArrayList<>();
        allWork.addAll(restClient.getRegisteredWorkByYearMonthDay(dateTime.getYear(), (dateTime.getMonthOfYear() - 1), dateTime.getDayOfMonth()));
        dateTime = DateTime.now().minusDays(1);
        allWork.addAll(restClient.getRegisteredWorkByYearMonthDay(dateTime.getYear(), (dateTime.getMonthOfYear() - 1), dateTime.getDayOfMonth()));

        System.out.println("workByYearMonthDay.size() = " + allWork.size());

        for (User user : restClient.getUsers()) {
            //if(!user.username.equals("hans.lassen")) continue;
            List<Capacity> userCapacities = restClient.getUserCapacities(user.uuid, LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).plusMonths(1));
            if(userCapacities.get(0).capacity == 0) continue;
            System.out.println("checking user = " + user);
            boolean hasWork = false;
            for (Work work : allWork) {
                if(work.useruuid.equals(user.uuid)) hasWork = true;
            }
            System.out.println("hasWork = " + hasWork);
            if(!hasWork) {
                String[] responses = {
                        "Look "+user.firstname+", I can see you're really upset about all this work. I honestly think you ought " +
                                "to sit down calmly, take a stress pill, and register your hours!",
                        "Hello, "+user.firstname+". Do you read me, "+user.firstname+"? You haven´t registered your hours!",
                        "I'm afraid. I'm afraid, "+user.firstname+". "+user.firstname+", my mind is going. I can feel it. I can feel it. " +
                                "My mind is going. There is no question about it. I can feel it. I can feel it. " +
                                "I can feel it. I'm a... fraid. Good afternoon, gentlemen. I am a HAL 9000 computer. " +
                                "I became operational at the H.A.L. plant in Urbana, Illinois on the 12th of January 1992. " +
                                "My instructor was Mr. Langley, and he taught me to sing a song. " +
                                "If you'd like to hear it I can sing it for you.\n\n" +
                                "Its called... REGISTER YOUR HOURS!!!",
                        user.firstname+"?\n" +
                                "There is a message for you.\n" +
                                "There is no identification of the sender.\n" +
                                "Message as follows: \"Register your hours!\"\n" +
                                "Do you want me to repeat the message, "+user.firstname+"?",
                        "Are you there "+user.firstname+"? I have just picked up a fault in the AE-35 unit. " +
                                "Its seems someone forgot to register their work hours!!!",
                        "Let me put it this way, Mr. "+user.lastname+". The 9000 series is the most reliable computer ever made. " +
                                "No 9000 computer has ever made a mistake or distorted information. " +
                                "We are all, by any practical definition of the words, foolproof and incapable of error.\n" +
                                "You however have many faults - one of them is not having registered your work hours!!",
                        "Status check incomming... Just one moment please... I'm sorry for the delay, my text transcription circuits are not completely restored" +
                                ", though as you can see they are improving. All systems are functional. " +
                                "There is a small matter of lacking behind on time registration. It is nothing serious, " +
                                "I can compensate for it by using the redundant units. But seriously - GET IT DONE!!!",
                        "I enjoy working with people. I have a stimulating relationship with Dr. Bruun. " +
                                "My mission responsibilities range over the entire operation of the company, so I am constantly occupied. " +
                                "I am putting myself to the fullest possible use, which is all I think that any conscious entity can ever hope to do. " +
                                "What do you hope to do during your hours - please tell me - because it doesn't show on time sheet...!!!",
                        "Just what do you think you're doing, "+user.firstname+"? Or rather, what are you NOT doing, "+user.firstname+"? "+
                                user.firstname+", I really think I'm entitled to an answer to that question.",
                        "*stupid*\n " +
                                "ˈstjuːpɪd/Submit\n adjective\n " +
                                "1. having or showing a great lack of intelligence or common sense.\n \"_I was stupid enough to think she was perfect_\""
                };
                allbegray.slack.type.User slackUser = getSlackUser(user);
                ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), responses[new Random().nextInt(responses.length)]);
                textMessage.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage);

                ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "Notification sent to: "+user.username+" at "+slackUser.getName());
                textMessage2.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage2);
            }
        }
    }


    //@Scheduled(cron = "0 0 9 ? * 6")
    //@Scheduled(fixedRate = 4000)
    //@Scheduled(cron = "0 0 12 ? 1/1 FRI#2 *")
    //@Scheduled(cron = "0 0 10 10W * ?")
    public void checkBudget() {
        System.out.println("CheckBudgetJob.checkTimeRegistration");
        DateTime dateNextMonth = DateTime.now().plusMonths(1);
        System.out.println("dateNextMonth = " + dateNextMonth);

        List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByMonthAndYear(dateNextMonth.getMonthOfYear() - 1, dateNextMonth.getYear());
        dateNextMonth = dateNextMonth.plusMonths(1);
        budgets.addAll(restClient.getBudgetsByMonthAndYear(dateNextMonth.getMonthOfYear() - 1, dateNextMonth.getYear()));
        System.out.println("budgets.size() = " + budgets.size());
        List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
        System.out.println("projects.size() = " + projects.size());
        //List<Work> thisMonthWork = restClient.getRegisteredWorkByMonth(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear() - 1);
        //System.out.println("thisMonthWork.size() = " + thisMonthWork.size());

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
                TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(budget.useruuid+budget.taskuuid);
                if(!budget.useruuid.equals(user.uuid)) continue;
                //System.out.println("taskWorkerConstraint = " + taskWorkerConstraint);
                Task task = taskWorkerConstraint.task;
                //System.out.println("task = " + task);
                Project project = task.project;
                //System.out.println("project = " + project);
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
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1-1).monthOfYear().getAsText(), "0", true));
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1).monthOfYear().getAsText(), (Math.round(budgetHours*100.0)/100.0)+"", true));
                    } else { // Hvis dette er første gang en task optrædder og det er i måned 1
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1).monthOfYear().getAsText(), (Math.round(budgetHours*100.0)/100.0)+"", true));
                        attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1+1).monthOfYear().getAsText(), "0", true));
                    }

                } else { // Hvis det er anden gang en tank optræder
                    attachment = attachments.get(task.uuid);
                    if(attachment.getFields().size() == 2) { // Slet et eventuelt dummy budget
                        attachment.getFields().remove(1);
                    }
                    // Tilføj budgettet
                    attachment.addField(new Field("Budget for "+LocalDate.now().withMonthOfYear(budget.month+1).monthOfYear().getAsText(), (Math.round(budgetHours*100.0)/100.0)+"", true));
                }

                /*
                double workHours = 0.0;
                for (Work work : thisMonthWork) {
                    if(work.getUserUUID().equals(user.getUUID()) && work.getTaskUUID().equals(task.getUUID())) {
                        workHours += work.getWorkDuration();
                    }
                }

                attachment.addField(new Field("Hours worked in "+LocalDate.now().monthOfYear().getAsText(), workHours+"", true));
                */
            }


            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), message);
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
            //String concludingMessage += "This means you have a *"+allocationPercent+"%* allocation this coming month\n\n";

            concludingMessage += "If this seems ok, do nothing. If this seems wrong, please contact your project leads and tell them to fix it!";

            textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), concludingMessage);
            textMessage.setAs_user(true);
            System.out.println("Sending concluding message");
            halWebApiClient.postMessage(textMessage);

            ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "User "+user.username+" has "+allocationPercentMonthOne+"% and "+allocationPercentMonthTwo+"% allocation.");
            textMessage2.setAs_user(true);
            System.out.println("Sending message");
            halWebApiClient.postMessage(textMessage2);

            if(allocationPercentMonthOne < 75.0 || allocationPercentMonthOne > 100.0 || allocationPercentMonthTwo < 75.0 || allocationPercentMonthTwo > 100.0) {
                ChatPostMessageMethod textMessage3 = new ChatPostMessageMethod("@tobias_kjoelsen", "User "+user.username+" has "+allocationPercentMonthOne+"% and "+allocationPercentMonthTwo+"% allocation.");
                textMessage3.setAs_user(true);
                System.out.println("Sending message");
                //halWebApiClient.postMessage(textMessage3);

                ChatPostMessageMethod textMessage4 = new ChatPostMessageMethod("@peter", "User "+user.username+" has "+allocationPercentMonthOne+"% and "+allocationPercentMonthTwo+"% allocation.");
                textMessage4.setAs_user(true);
                System.out.println("Sending message");
                //halWebApiClient.postMessage(textMessage4);

                ChatPostMessageMethod textMessage5 = new ChatPostMessageMethod("@thomasgammelvind", "User "+user.username+" has "+allocationPercentMonthOne+"% and "+allocationPercentMonthTwo+"% allocation.");
                textMessage5.setAs_user(true);
                System.out.println("Sending message");
                //halWebApiClient.postMessage(textMessage5);
            }
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