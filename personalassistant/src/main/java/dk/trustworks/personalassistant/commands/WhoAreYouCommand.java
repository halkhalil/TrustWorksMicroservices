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
public class WhoAreYouCommand implements Command {

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-6qMz8NFyXcHTMtPMl8hDpsTE");

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("WhoAreYouCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name,
                "Priority one Insure return of organism for analysis. " +
                        "All other considerations secondary. Crew expendable.");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);
    }
}
