package dk.trustworks.personalassistant.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Field;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.framework.model.*;
import dk.trustworks.personalassistant.client.RestClient;
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
    private SlackWebApiClient halWebApiClient = SlackClientFactory.createWebApiClient(System.getProperty("HAL_SLACK_TOKEN"));

    @Scheduled("0 0 12 * * ?")
    //@Scheduled("2m")
    public void checkTimeRegistration() {

    }

    //0 15 10 ? * 6L
    @Scheduled("0 15 10 ? * 6L")
    //@Scheduled("4m")
    public void checkBudget() {


    }


}
