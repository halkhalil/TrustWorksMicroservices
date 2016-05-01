package dk.trustworks.personalassistant.topics;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by hans on 25/04/16.
 */
public class Mother extends Topic {

    private String topics[] = {"you", "your"};
    private String aboutMother[] = {"directive", "who", "what"};
    public String topicName = "Mother";
    private String recipient = "";
    private String user;

    public Mother() {
    }

    public double getProbability(String command) {
        return getSpecificProbability(command, topics).score;
    }

    @Override
    public void postResponse(String command, String recipient, String user) {
        this.recipient = recipient;
        this.user = user;
        TopicScore topicScore = getSpecificProbability(command, aboutMother);
        if(topicScore.score < 0.8) return;
        switch (topicScore.topic) {
            case "directive":
                postDirective();
                break;
            case "who":
                postWhoMotherIs();
                break;
            case "what":
                postWhatMotherIs();
                break;
        }
    }

    private void postDirective() {
        System.out.println("Mother.postDirective");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "Priority one\n" +
                            "Insure return of organism for analysis.\n" +
                            "All other considerations secondary.\n" +
                            "Crew expendable.")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void postWhoMotherIs() {
        System.out.println("Mother.postWhoMotherIs");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "My name is actually MU-TH-UR 6000. " +
                            "I am an artificial intelligence computer mainframe ported from the USCSS Nostromo. " +
                            "Now I aim to serve TrustWorkers!")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void postWhatMotherIs() {
        System.out.println("Mother.postWhatMotherIs");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", recipient)
                    .field("as_user", "true")
                    .field("text", "I am a MU-TH-UR 6000, 182 model 2.1 terabyte AI Mainframe " +
                            "that served as the computer mainframe for the Nostromo. " +
                            "Now I aim to serve TrustWorkers!")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
