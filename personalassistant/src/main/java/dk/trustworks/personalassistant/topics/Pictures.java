package dk.trustworks.personalassistant.topics;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.personalassistant.dropbox.DropboxAPI;

/**
 * Created by hans on 25/04/16.
 */
public class Pictures extends Topic {

    private DropboxAPI dropboxAPI;
    private String topics[] = {"pictures", "photos"};
    private String photoTopics[] = {"Paris", "Costa"};
    public String topicName = "pictures";
    private String recipient;
    private String user;

    public Pictures() {
        dropboxAPI = new DropboxAPI();
    }

    @Override
    public void postResponse(String command, String recipient, String user) {
        this.recipient = recipient;
        this.user = user;
        TopicScore topicScore = getSpecificProbability(command, photoTopics);
        if(topicScore.score < 0.8) return;
        switch (topicScore.topic.toLowerCase()) {
            case "paris":
                showParis();
                break;
            case "costa":
                showCostaRica();
                break;
        }
    }

    public double getProbability(String command) {
        return getSpecificProbability(command, topics).score;
    }

    private void showParis() {
        System.out.println("Pictures.showParis");
        try {
            Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "Attention. Finding a random photo from Paris - ETA 10 seconds")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        byte[] randomFile = dropboxAPI.getRandomFile("/Shared/TrustWorks/Billeder/Paris");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/files.upload")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("file", randomFile, "image.jpeg")
                    .field("filetype", "jpeg")
                    .field("channels", recipient)
                    .field("filename", "image.jpg")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void showCostaRica() {
        System.out.println("Pictures.showCostaRica");
        try {
            Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "Attention. Finding a random photo from Paulas trip to Costa Rica - ETA 10 seconds")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        byte[] randomFile = dropboxAPI.getRandomFile("/Shared/TrustWorks/Billeder/andet/costa_rica");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/files.upload")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("file", randomFile, "image.jpeg")
                    .field("filetype", "jpeg")
                    .field("channels", recipient)
                    .field("filename", "image.jpg")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
