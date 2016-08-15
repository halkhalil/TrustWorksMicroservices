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
public class WhatIsYourDirectiveCommand implements Command {

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getProperty("SLACK_TOKEN"));

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("WhatIsYourDirectiveCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        if(!command.channel_name.equals("directmessage"))
            SlackResponseClient.sendResponse(command.response_url, new SlackMessage("See my response in a direct message from me", "ephemeral"));

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name,
                "Priority one Insure return of organism for analysis. " +
                        "All other considerations secondary. Crew expendable.");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);
    }
}
