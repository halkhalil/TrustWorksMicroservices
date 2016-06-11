package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.dropbox.core.v2.files.SearchMatch;
import dk.trustworks.personalassistant.client.SlackResponseClient;
import dk.trustworks.personalassistant.dropbox.DropboxAPI;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;
import dk.trustworks.personalassistant.search.indexers.IndexItem;
import dk.trustworks.personalassistant.search.indexers.Searcher;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 31/05/16.
 */
public class FileSearchCommand implements Command {

    public static final String INDEX_DIR = "~/index";
    private final DropboxAPI dropboxAPI;

    private final Map<String, String> libraries;

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getenv("SLACK_TOKEN"));

    public FileSearchCommand() {
        dropboxAPI = new DropboxAPI();
        libraries = new HashMap<>();
        libraries.put("costa rica", "/Shared/TrustWorks/Billeder/andet/costa_rica");
        libraries.put("paris", "/Shared/TrustWorks/Billeder/Paris");
    }

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("TemplateCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        SlackResponseClient.sendResponse(command.response_url, new SlackMessage("See my response in a direct message from me", "ephemeral"));

        String searchLocation = (intentOutcome.getParameters().getAdditionalProperties().get("docstores")!=null)?intentOutcome.getParameters().getAdditionalProperties().get("docstores").toString():"dropbox";
        if(intentOutcome.getParameters().getAdditionalProperties().get("any")==null) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, intentOutcome.getFulfillment().getSpeech());
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }
        String searchString = intentOutcome.getParameters().getAdditionalProperties().get("any").toString();

        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "Searching for: "+searchString);
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);

        List<SearchMatch> searchMatches = dropboxAPI.searchFiles(searchString);

        String searchResult = "Found:\n";

        for (int i = 0; i < 5; i++) {
            SearchMatch searchMatch = searchMatches.get(0);
            System.out.println("item.getPath() = " + searchMatch.getMetadata().getPathLower());
            String fileURL = dropboxAPI.getFileURL(searchMatch.getMetadata().getPathDisplay());
            searchResult += "<"+fileURL+"|"+searchMatch.getMetadata().getPathDisplay()+">\n";
        }

        ChatPostMessageMethod searchMessage = new ChatPostMessageMethod("@"+command.user_name, searchResult);
        searchMessage.setAs_user(true);
        webApiClient.postMessage(searchMessage);
    }
}