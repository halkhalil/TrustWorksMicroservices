package dk.trustworks.personalassistant.commands;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Color;
import allbegray.slack.type.Field;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.dropbox.core.v2.files.SearchMatch;
import dk.trustworks.personalassistant.client.SlackResponseClient;
import dk.trustworks.personalassistant.dropbox.DropboxAPI;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 31/05/16.
 */
public class FileSearchCommand implements Command {

    private final DropboxAPI dropboxAPI;

    private SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(System.getenv("SLACK_TOKEN"));

    public FileSearchCommand() {
        dropboxAPI = new DropboxAPI();
    }

    @Override
    public void execute(Result intentOutcome, SlackSlashCommand command) {
        System.out.println("FileSearchCommand.execute");
        System.out.println("intentOutcome = [" + intentOutcome + "], command = [" + command + "]");

        if(!command.channel_name.equals("directmessage"))
            SlackResponseClient.sendResponse(command.response_url, new SlackMessage("See my response in a direct message from me", "ephemeral"));

        String searchLocation = (intentOutcome.getParameters().getAdditionalProperties().get("docstores")!=null)?intentOutcome.getParameters().getAdditionalProperties().get("docstores").toString():"dropbox";
        if(intentOutcome.getParameters().getAdditionalProperties().get("any")==null) {
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, intentOutcome.getFulfillment().getSpeech());
            textMessage.setAs_user(true);
            webApiClient.postMessage(textMessage);
            return;
        }
        String searchString = intentOutcome.getParameters().getAdditionalProperties().get("any").toString();

        /*
        ChatPostMessageMethod textMessage = new ChatPostMessageMethod("@"+command.user_name, "Searching for: "+searchString);
        textMessage.setAs_user(true);
        webApiClient.postMessage(textMessage);
        */
        SlackResponseClient.sendResponse(command.response_url, new SlackMessage("Searching for: "+searchString, "ephemeral"));

        List<SearchMatch> searchMatches = dropboxAPI.searchFiles(searchString, 0);

        Map<String, Integer> folderOccourrences = new HashMap<>();
        for (SearchMatch searchMatch : searchMatches) {
            for (String filePathName : searchMatch.getMetadata().getPathDisplay().split("/")) {
                if(!folderOccourrences.containsKey(filePathName)) folderOccourrences.put(filePathName, 0);
                folderOccourrences.put(filePathName, folderOccourrences.get(filePathName) + 1);
            }
        }

        Map<SearchMatch, Integer> searchMatchScore = new HashMap<>();
        for (SearchMatch searchMatch : searchMatches) {
            int score = 0;
            for (String filePathName : searchMatch.getMetadata().getPathDisplay().split("/")) {
                score += folderOccourrences.get(filePathName);
            }
            searchMatchScore.put(searchMatch, score);
        }

        int resultSize = 5;
        List<SearchMatch> resultList = new ArrayList<>(resultSize);
        int prevScore = 0;
        SearchMatch prevMatch = null;
        boolean directionUp = true;
        for (SearchMatch searchMatch : searchMatchScore.keySet()) {
            Integer currentScore = searchMatchScore.get(searchMatch);
            if(directionUp) {
                if (currentScore < prevScore) {
                    resultList.add(prevMatch);
                    directionUp = false;
                }
            } else {
                if (currentScore > prevScore) {
                    resultList.add(prevMatch);
                    directionUp = true;
                }
            }
            prevScore = currentScore;
            prevMatch = searchMatch;
            if(resultList.size()>4) break;
        }

        String[] colors = {"#fcf585", "#fbb14d", "8fa78a", "#007163", "#2c586d"};

        ArrayList<Attachment> attachments = new ArrayList<>();
        for (SearchMatch searchMatch : resultList) {
            System.out.println("item.getPath() = " + searchMatch.getMetadata().getPathLower());
            String fileURL = dropboxAPI.getFileURL(searchMatch.getMetadata().getPathDisplay());
            //searchResult += "<"+fileURL+"|"+searchMatch.getMetadata().getPathDisplay()+">\n";
            Attachment attachment = new Attachment();
            attachment.setTitle(searchMatch.getMetadata().getName());
            attachment.setTitle_link(fileURL);
            attachment.setText(searchMatch.getMetadata().getPathDisplay());
            attachment.setColor(colors[resultSize -1]);
            ArrayList<Field> fields = new ArrayList<>();
            Field field = new Field("Name or Content", searchMatch.getMatchType().name(), true);
            fields.add(field);
            attachment.setFields(fields);
            attachments.add(attachment);
            if(resultSize-- == 0) break;
        }

        ChatPostMessageMethod searchMessage = new ChatPostMessageMethod("@"+command.user_name, "Found:");
        searchMessage.setAs_user(true);
        searchMessage.setAttachments(attachments);
        webApiClient.postMessage(searchMessage);
    }

    /*
    "match_type": {
        ".tag": "both"
      },
      "metadata": {
        ".tag": "file",
        "name": "FL-#152312-v3A-Målarkitektur_-_kravspecifikation (1).DOC",
        "path_lower": "/shared/projekt/miljøstyrelsen/pde/fl-#152312-v3a-målarkitektur_-_kravspecifikation (1).doc",
        "path_display": "/Shared/projekt/miljøstyrelsen/PDE/FL-#152312-v3A-Målarkitektur_-_kravspecifikation (1).DOC",
        "parent_shared_folder_id": "698381028",
        "id": "id:jljljgzEAkwAAAAAAAC7EA",
        "client_modified": "1979-12-31T23:00:00Z",
        "server_modified": "2015-01-09T09:42:07Z",
        "rev": "135c229a072e4",
        "size": 1439744,
        "sharing_info": {
          "read_only": false,
          "parent_shared_folder_id": "698381028",
          "modified_by": "dbid:AACHqWWYQLAJRImS4BFAcHmrGkbS1wVWeYU"
        }
      }
     */
}