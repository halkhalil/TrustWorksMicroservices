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
