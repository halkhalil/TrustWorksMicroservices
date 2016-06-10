package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.dropbox.DropboxAPI;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 31/05/16.
 */
public class PhotosCommand implements Command {

    private final DropboxAPI dropboxAPI;

    private final Map<String, String> libraries;

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getenv("SLACK_TOKEN"));

    public PhotosCommand() {
        dropboxAPI = new DropboxAPI();
        libraries = new HashMap<>();
        libraries.put("costa rica", "/Shared/TrustWorks/Billeder/andet/costa_rica");
        libraries.put("paris", "/Shared/TrustWorks/Billeder/Paris");
    }

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("PhotosCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "Attention. Finding a random photo from Paris - ETA 10 seconds");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);

        byte[] randomFile = dropboxAPI.getRandomFile(libraries.get(intentOutcome.getParameters().getGeoCity().toLowerCase()));

        webApiClient.uploadFile(new ByteArrayInputStream(randomFile), "jpeg", "image.jpg", "image.jpg", "", "@"+command.user_name);
    }
}