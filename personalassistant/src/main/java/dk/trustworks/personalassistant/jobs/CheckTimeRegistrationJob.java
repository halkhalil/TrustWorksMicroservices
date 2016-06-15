package dk.trustworks.personalassistant.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.client.RestClient;
import dk.trustworks.personalassistant.dto.timemanager.User;
import dk.trustworks.personalassistant.dto.timemanager.Work;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jooby.quartz.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 15/06/16.
 */
public class CheckTimeRegistrationJob {

    private final RestClient restClient = new RestClient();
    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getenv("SLACK_TOKEN"));

    //@Scheduled("0 0 12* ?")
    @Scheduled("5m")
    public void checkTimeRegistration() {
        System.out.println("CheckTimeRegistrationJob.checkTimeRegistration");
        DateTime dateTime = DateTime.now();
        if(dateTime.getDayOfWeek() > 5) return; // do not check in weekends
        System.out.println("This is not in the weekend");

        dateTime = (dateTime.getDayOfWeek() == 1) ? dateTime.minusDays(3) : dateTime.minusDays(1);
        System.out.println("dateTime = " + dateTime);

        List<Work> workByYearMonthDay = restClient.getRegisteredWorkByYearMonthDay(dateTime.getYear(), (dateTime.getMonthOfYear() - 1), dateTime.getDayOfMonth());
        System.out.println("workByYearMonthDay.size() = " + workByYearMonthDay.size());

        for (User user : restClient.getUsers()) {
            if(!user.getUsername().equals("hans.lassen")) return;
            System.out.println("checking user = " + user);
            boolean hasWork = false;
            for (Work work : workByYearMonthDay) {
                if(work.getUserUUID().equals(user.getUUID())) hasWork = true;
            }
            System.out.println("hasWork = " + hasWork);
            if(!hasWork) {
                int levenshsteinScore = 100;
                allbegray.slack.type.User slackUser = null;
                for (allbegray.slack.type.User slackUserIteration : webApiClient.getUserList()) {
                    int levenshteinDistance = StringUtils.getLevenshteinDistance(user.getFirstname() + " " + user.getLastname(), slackUserIteration.getProfile().getReal_name());
                    if(levenshteinDistance < levenshsteinScore) {
                        levenshsteinScore = levenshteinDistance;
                        slackUser = slackUserIteration;
                    }
                }
                System.out.println("Identified slackUser.getName() = " + slackUser.getName());
                ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), "Please remember to update your time sheet!");
                textMessage.setAs_user(true);
                System.out.println("Sending message");
                webApiClient.postMessage(textMessage);
            }
        }
    }
}
