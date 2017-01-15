package dk.trustworks.botmanager.slack.command;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.botmanager.network.dropbox.DropboxAPI;
import dk.trustworks.botmanager.nlp.dto.Result;

import java.io.ByteArrayInputStream;

/**
 * Created by hans on 31/05/16.
 */
public class TemplateCommand {

    private final DropboxAPI dropboxAPI;

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getProperty("SLACK_TOKEN"));

    public TemplateCommand() {
        dropboxAPI = new DropboxAPI();
    }

    public void execute(Result intentOutcome, String channelId) {
        System.out.println("TemplateCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], channelId = [" + channelId + "]");

        String color = (intentOutcome.getParameters().getAdditionalProperties().get("color")!=null)?intentOutcome.getParameters().getAdditionalProperties().get("color").toString():"blue";
        if(intentOutcome.getParameters().getAdditionalProperties().get("template-type")==null) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod(channelId, intentOutcome.getFulfillment().getSpeech());
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }
        String templateType = intentOutcome.getParameters().getAdditionalProperties().get("template-type").toString();
        Object templateSizeParam = intentOutcome.getParameters().getAdditionalProperties().get("template-size");
        String templateSize = (templateSizeParam == null||templateSizeParam.toString().equals(""))?"Widescreen":templateSizeParam.toString();

        if(templateType.equals("word")) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod(channelId, "I am sorry. I don't know where our word templates are located!");
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }

        if(templateType.equals("excel")) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod(channelId, "I am sorry. I don't know where our excel templates are located!");
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod(channelId, "Attention. Uploading "+templateType+" template - ETA 5 seconds");
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);

        byte[] randomFile = dropboxAPI.getSpecificFile("/Shared/Templates/TW Præsentation/TW_SKABELONER/"+templateSize+"/TW-"+color.toUpperCase()+".pptx");

        webApiClient.uploadFile(new ByteArrayInputStream(randomFile), "pptx", "TrustWorks_template_"+color.toLowerCase()+".pptx", "TrustWorks_template_"+color.toLowerCase()+".pptx", "", channelId);
    }
}