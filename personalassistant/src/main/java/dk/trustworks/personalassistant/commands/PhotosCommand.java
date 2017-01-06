package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import dk.trustworks.personalassistant.client.SlackResponseClient;
import dk.trustworks.personalassistant.client.dropbox.DropboxAPI;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
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

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getProperty("SLACK_TOKEN"));

    public PhotosCommand() {
        dropboxAPI = new DropboxAPI();
        libraries = new HashMap<>();
        libraries.put("costa rica", "/Shared/TrustWorks/Billeder/andet/costa_rica");
        libraries.put("paris", "/Shared/TrustWorks/Billeder/Paris");
        libraries.put("new york", "/Shared/TrustWorks/Billeder/New York");
        libraries.put("madrid", "/Shared/TrustWorks/Billeder/Madrid");
        libraries.put("vr", "/Shared/TrustWorks/Billeder/VR demo");
        libraries.put("carpe diem", "/carpediem");
        libraries.put("tobias kjølsen", "/tobiaskjoelsen");
    }

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("PhotosCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        /*
        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "Attention. Finding a random photo from Paris - ETA 10 seconds");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);
        */
        String location = "";
        if(!(intentOutcome.getParameters().getGeoCity()==null) && !intentOutcome.getParameters().getGeoCity().trim().equals("")) {
            location = intentOutcome.getParameters().getGeoCity();
        }
        if(!(intentOutcome.getParameters().getGeoCountry()==null) && !intentOutcome.getParameters().getGeoCountry().trim().equals("")) {
            location = intentOutcome.getParameters().getGeoCountry();
        }
        if(!(intentOutcome.getParameters().getAdditionalProperties().get("subject")==null) && !intentOutcome.getParameters().getAdditionalProperties().get("subject").toString().trim().equals("")) {
            location = intentOutcome.getParameters().getAdditionalProperties().get("subject").toString();
        }


        SlackResponseClient.sendResponse(command.response_url, new SlackMessage("Attention. Finding a random photo from "+location+" - ETA 10 seconds", "ephemeral"));

        byte[] randomFile = dropboxAPI.getRandomFile(libraries.get(location.toLowerCase()));

        webApiClient.uploadFile(new ByteArrayInputStream(randomFile), "jpeg", "image.jpg", "image.jpg", "", command.channel_id);
    }
}