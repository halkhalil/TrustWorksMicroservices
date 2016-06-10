package dk.trustworks.personalassistant.client;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;

/**
 * Created by hans on 31/05/16.
 */
public class SlackResponseClient {

    public static void sendResponse(String responseUrl, SlackMessage slackMessage) {
        System.out.println("SlackResponseClient.sendResponse");
        System.out.println("responseUrl = [" + responseUrl + "], slackMessage = [" + slackMessage + "]");
        try {
            Unirest.post(responseUrl).body(slackMessage).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
