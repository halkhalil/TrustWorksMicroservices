package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
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
public class TemplateCommand implements Command {

    private final DropboxAPI dropboxAPI;

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getProperty("SLACK_TOKEN"));

    public TemplateCommand() {
        dropboxAPI = new DropboxAPI();
        Map<String, String> libraries = new HashMap<>();
        libraries.put("costa rica", "/Shared/TrustWorks/Billeder/andet/costa_rica");
        libraries.put("paris", "/Shared/TrustWorks/Billeder/Paris");
    }

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("TemplateCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        if(!command.channel_name.equals("directmessage"))
            SlackResponseClient.sendResponse(command.response_url, new SlackMessage("See my response in a direct message from me", "ephemeral"));

        String color = (intentOutcome.getParameters().getAdditionalProperties().get("color")!=null)?intentOutcome.getParameters().getAdditionalProperties().get("color").toString():"blue";
        if(intentOutcome.getParameters().getAdditionalProperties().get("template-type")==null) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, intentOutcome.getFulfillment().getSpeech());
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }
        String templateType = intentOutcome.getParameters().getAdditionalProperties().get("template-type").toString();
        Object templateSizeParam = intentOutcome.getParameters().getAdditionalProperties().get("template-size");
        String templateSize = (templateSizeParam == null||templateSizeParam.toString().equals(""))?"Widescreen":templateSizeParam.toString();

        if(templateType.equals("word")) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "I am sorry. I don't know where our word templates are located!");
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }

        if(templateType.equals("excel")) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "I am sorry. I don't know where our excel templates are located!");
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }

        /*
        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "Attention. Uploading "+templateType+" template - ETA 5 seconds");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);
        */
        SlackResponseClient.sendResponse(command.response_url, new SlackMessage("Attention. Uploading "+templateType+" template - ETA 5 seconds", "ephemeral"));

        byte[] randomFile = dropboxAPI.getSpecificFile("/Shared/Templates/TW Præsentation/TW_TEMPLATES/"+templateSize+"/TW-"+color.toUpperCase()+".pptx");

        webApiClient.uploadFile(new ByteArrayInputStream(randomFile), "pptx", "TrustWorks_template_"+color.toLowerCase()+".pptx", "TrustWorks_template_"+color.toLowerCase()+".pptx", "", "@"+command.user_name);
    }
}