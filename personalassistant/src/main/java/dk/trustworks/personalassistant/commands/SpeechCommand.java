package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.client.SlackResponseClient;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by hans on 31/05/16.
 */
public class SpeechCommand implements Command {

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getenv("SLACK_TOKEN"));

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("SpeechCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        if(!command.channel_name.equals("directmessage"))
            SlackResponseClient.sendResponse(command.response_url, new SlackMessage("See my response in a direct message from me", "ephemeral"));

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, StringEscapeUtils.unescapeJava(intentOutcome.getFulfillment().getSpeech()));
        textMessage.setAs_user(true);
        System.out.println("textMessage = " + textMessage.getText());
        webApiClient.postMessage(textMessage);
    }
}
