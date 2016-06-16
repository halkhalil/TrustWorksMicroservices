package dk.trustworks.personalassistant.client;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;

import java.io.ByteArrayInputStream;

/**
 * Created by hans on 10/06/16.
 */
public class SlackWebApiClient {

    //private final static String token = "xoxb-37490350945-6qMz8NFyXcHTMtPMl8hDpsTE";
    private final static allbegray.slack.webapi.SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-6qMz8NFyXcHTMtPMl8hDpsTE");

    public static void sendTextMessage(String from, String message) {
        System.out.println("SlackWebApiClient.sendTextMessage");
        System.out.println("from = [" + from + "], message = [" + message + "]");
        //allbegray.slack.webapi.SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-6qMz8NFyXcHTMtPMl8hDpsTE");
        ChatPostMessageMethod chatPostMessageMethod = new ChatPostMessageMethod("@"+from, message);
        webApiClient.postMessage(chatPostMessageMethod);
    }

    public static void sendFileMessage(String from, SlackMessage message) {
        System.out.println("SlackWebApiClient.sendFileMessage");
        System.out.println("from = [" + from + "], message = [" + message + "]");

        //ChatPostMessageMethod chatPostMessageMethod = new ChatPostMessageMethod(from, message);
        webApiClient.uploadFile(new ByteArrayInputStream(message.file), message.filetype, message.filename, message.filename, "", "@"+from);
    }
}
