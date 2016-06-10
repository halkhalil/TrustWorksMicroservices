package dk.trustworks.personalassistant.client;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackWebhookFileMessage;

import java.io.ByteArrayInputStream;

/**
 * Created by hans on 05/06/16.
 */
public class SlackWebhookClient {

    private final static String webhookURL = "https://hooks.slack.com/services/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";

    private final static SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-6qMz8NFyXcHTMtPMl8hDpsTE");

    public static void sendTextMessage(String from, String message) {
        System.out.println("SlackWebhookClient.sendTextMessage");
        System.out.println("from = [" + from + "], message = [" + message + "]");

        ChatPostMessageMethod chatPostMessageMethod = new ChatPostMessageMethod("@"+from, message);
        webApiClient.postMessage(chatPostMessageMethod);
    }

    public static void sendFileMessage(String from, SlackMessage message) {
        System.out.println("SlackWebhookClient.sendFileMessage");
        System.out.println("from = [" + from + "], message = [" + message + "]");
        //SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-6qMz8NFyXcHTMtPMl8hDpsTE");
        //ChatPostMessageMethod chatPostMessageMethod = new ChatPostMessageMethod(from, message);
        webApiClient.uploadFile(new ByteArrayInputStream(message.file), message.filetype, message.filename, message.filename, "", "@"+from);
    }

    public static void sendMessage(String from, SlackMessage slackMessage, boolean isFile) {
        System.out.println("SlackResponseClient.sendResponse");
        System.out.println("from = [" + from + "], slackMessage = [" + slackMessage + "], isFile = [" + isFile + "]");

        if(!isFile) {
            //try {
                SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61");
                ChatPostMessageMethod chatPostMessageMethod = new ChatPostMessageMethod(from, "Attention. Finding a random photo from Paris - ETA 10 seconds");
                webApiClient.postMessage(chatPostMessageMethod);
                /*
                //SlackWebhookTextMessage message = new SlackWebhookTextMessage(from, slackMessage.text);
                Unirest.post("https://slack.com/api/chat.postMessage")
                        .header("accept", "application/json")
                        .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                        .field("channel", from)
                        .field("as_user", "true")
                        .field("text", "Attention. Finding a random photo from Paris - ETA 10 seconds")
                        .asJson();
                        */
                /*
                Unirest.post(webhookURL)
                        .header("accept", "application/json")
                        .body(message)
                        .asJson();
                        */
            //} catch (UnirestException e) {
              //  System.out.println("101");
            //    e.printStackTrace();
            //}
            return;
        }

        try {
            SlackWebhookFileMessage message = new SlackWebhookFileMessage(from, slackMessage.file, slackMessage.filetype, slackMessage.filename);

            Unirest.post("https://slack.com/api/files.upload")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("file", slackMessage.file, "image.jpeg")
                    .field("filetype", "jpeg")
                    .field("channels", from)
                    .field("filename", "image.jpg")
                    .asJson();

            /*
            Unirest.post(webhookURL)
                    .header("accept", "application/json")
                    .body(message)
                    .asJson();
                    */
        } catch (UnirestException e) {
            System.out.println("102");
            e.printStackTrace();
        }
    }
}
