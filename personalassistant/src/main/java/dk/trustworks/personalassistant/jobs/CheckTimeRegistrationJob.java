package dk.trustworks.personalassistant.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.client.RestClient;
import dk.trustworks.personalassistant.dto.timemanager.User;
import dk.trustworks.personalassistant.dto.timemanager.Work;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jooby.quartz.Scheduled;

import java.util.ArrayList;
import java.util.List;

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
                int levenshsteinScore = 100;
                allbegray.slack.type.User slackUser = null;
                for (allbegray.slack.type.User slackUserIteration : halWebApiClient.getUserList()) {
                    int levenshteinDistance = StringUtils.getLevenshteinDistance(user.getFirstname() + " " + user.getLastname(), slackUserIteration.getProfile().getReal_name());
                    System.out.println("slackUserIteration.getProfile().getReal_name() = " + slackUserIteration.getProfile().getReal_name());
                    System.out.println("levenshteinDistance = " + levenshteinDistance);
                    if(levenshteinDistance < levenshsteinScore) {
                        levenshsteinScore = levenshteinDistance;
                        slackUser = slackUserIteration;
                    }
                }
                System.out.println("Identified slackUser.getName() = " + slackUser.getName());
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
}
