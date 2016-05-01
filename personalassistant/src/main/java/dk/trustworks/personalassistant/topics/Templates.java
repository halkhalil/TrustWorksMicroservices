package dk.trustworks.personalassistant.topics;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.personalassistant.dropbox.DropboxAPI;

/**
 * Created by hans on 26/04/16.
 */
public class Templates extends Topic {

    private final DropboxAPI dropboxAPI;
    private final String topics[] = {"template", "document"};
    private final String templateTypes[] = {"powerpoint", "word", "excel"};
    private final String powerpointColors[] = {"blue", "green", "grey", "gray", "bw", "black"};
    public String topicName = "template";
    private String recipient;
    private String user;

    public Templates() {
        dropboxAPI = new DropboxAPI();
    }

    @Override
    public void postResponse(String command, String recipient, String user) {
        this.recipient = recipient;
        this.user = user;
        TopicScore topicScore = getSpecificProbability(command, templateTypes);
        if(topicScore.score < 0.8) return;
        switch (topicScore.topic) {
            case "powerpoint":
                TopicScore colorScore = getSpecificProbability(command, powerpointColors);
                String color;
                if(colorScore.score < 0.8) {
                    color = "BW";
                } else {
                    switch (colorScore.topic) {
                        case "blue":
                            color = "BLUE";
                            break;
                        case "green":
                            color = "GREEN";
                            break;
                        default:
                            color = "BW";
                            break;
                    }
                }
                postPowerPointTemplate(color);
                break;
        }
    }

    private void postPowerPointTemplate(String color) {
        byte[] randomFile = dropboxAPI.getSpecificFile("/Shared/Templates/TW Præsentation/TW_TEMPLATES/Widescreen/TW-"+color+".pptx");
        try {
            Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "Attention. Uploading powerpoint template - ETA 5 seconds")
                    .asJson();

            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/files.upload")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("file", randomFile, "TrustWorks_template_"+color.toLowerCase()+".pptx")
                    .field("channels", recipient)
                    //.field("filename", "TrustWorks_template_bred_format.pptx")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public double getProbability(String command) {
        return getSpecificProbability(command, topics).score;
    }
}
