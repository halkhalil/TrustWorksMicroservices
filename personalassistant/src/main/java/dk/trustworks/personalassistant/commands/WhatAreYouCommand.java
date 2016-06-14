package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;
import dk.trustworks.personalassistant.client.SlackResponseClient;

/**
 * Created by hans on 31/05/16.
 */
public class WhatAreYouCommand implements Command {

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getenv("SLACK_TOKEN"));

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("WhatAreYouCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        if(!command.channel_name.equals("directmessage"))
            SlackResponseClient.sendResponse(command.response_url, new SlackMessage("See my response in a direct message from me", "ephemeral"));

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name,
                "I am a MU-TH-UR 6000, 182 model 2.1 terabyte AI Mainframe that " +
                        "served as the computer mainframe for the Nostromo. " +
                        "Now I aim to serve TrustWorkers!");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);
    }
}
