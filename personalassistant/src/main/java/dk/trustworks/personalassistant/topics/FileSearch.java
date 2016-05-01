package dk.trustworks.personalassistant.topics;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.personalassistant.dropbox.DropboxAPI;
import dk.trustworks.personalassistant.search.indexers.IndexItem;
import dk.trustworks.personalassistant.search.indexers.Searcher;

import java.util.List;

/**
 * Created by hans on 25/04/16.
 */
public class FileSearch extends Topic {

    private static final String INDEX_DIR = "/Users/hans/index";
    private static final int DEFAULT_RESULT_SIZE = 5;

    private String topics[] = {"find", "search"};
    //private String aboutMother[] = {"directive", "who", "what"};
    public String topicName = "Mother";
    private String recipient;
    private String user;
    private final DropboxAPI dropboxAPI;

    public FileSearch() {
        dropboxAPI = new DropboxAPI();
    }

    public double getProbability(String command) {
        return getSpecificProbability(command, topics).score;
    }

    @Override
    public void postResponse(String command, String recipient, String user) {
        this.user = user;
        System.out.println("FileSearch.postResponse");
        System.out.println("command = [" + command + "], recipient = [" + recipient + "]");
        this.recipient = recipient;
        searchForFiles(command.split(":")[1].trim());
    }

    private void searchForFiles(String searchParameter) {
        System.out.println("FileSearch.searchForFiles");
        System.out.println("searchParameter = [" + searchParameter + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "Searching for: "+searchParameter)
                    .asJson();
            System.out.println("jsonResponse = " + jsonResponse.getBody().getObject().toString());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        String searchResult = "Found:\n";
        try {
            Searcher searcher = new Searcher(INDEX_DIR);
            List<IndexItem> result;
            result = searcher.findByContent(searchParameter, DEFAULT_RESULT_SIZE);
            for (IndexItem item : result) {
                String fileURL = dropboxAPI.getFileURL(item.getPath());
                System.out.println("item.getPath() = " + item.getPath());
                System.out.println("dropboxAPI.getFileURL(item.getPath()) = " + fileURL);
                searchResult += "<"+fileURL+"|"+item.getTitle()+">\n";
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        // https://api.dropboxapi.com/1/shares/auto/<path>

        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", searchResult)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
