package dk.trustworks.botmanager.slack;

import dk.trustworks.botmanager.nlp.dto.Result;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by hans on 06/01/2017.
 */
@Component
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    private final Map<String, String> channelValues = new HashMap<>();

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    /**
     * Invoked when the bot receives a direct mention (@botname: message)
     * or a direct message. NOTE: These two event types are added by jbot
     * to make your task easier, Slack doesn't have any direct way to
     * determine these type of events.
     *
     * @param session
     * @param event
     */

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
        reply(session, event, new Message("Hi, I am " + slackService.getCurrentUser().getName()));
    }

    /**
     * Invoked when bot receives an event of type message with text satisfying
     * the pattern {@code ([a-z ]{2})(\d+)([a-z ]{2})}. For example,
     * messages like "ab12xy" or "ab2bc" etc will invoke this method.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.MESSAGE, pattern = "^([a-z ]{2})(\\d+)([a-z ]{2})$")
    public void onReceiveMessage(WebSocketSession session, Event event, Result nlpResult, Matcher matcher) {
        reply(session, event, new Message("First group: " + matcher.group(0) + "\n" +
                "Second group: " + matcher.group(1) + "\n" +
                "Third group: " + matcher.group(2) + "\n" +
                "Fourth group: " + matcher.group(3)));
    }

    /**
     * Invoked when an item is pinned in the channel.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.PIN_ADDED)
    public void onPinAdded(WebSocketSession session, Event event) {
        reply(session, event, new Message("Thanks for the pin! You can find all pinned items under channel details."));
    }

    /**
     * Invoked when bot receives an event of type file shared.
     * NOTE: You can't reply to this event as slack doesn't send
     * a channel id for this event type. You can learn more about
     * <a href="https://api.slack.com/events/file_shared">file_shared</a>
     * event from Slack's Api documentation.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
        logger.info("File shared: {}", event);
    }


    @Controller(events = {EventType.DIRECT_MESSAGE}, pattern = "(timemanager.password)", next = "enterPassword")
    public void changePassword(WebSocketSession session, Event event, Result nlpResult) {
        System.out.println("nlpResult = " + nlpResult);
        startConversation(event, "enterPassword");   // start conversation
        reply(session, event, new Message(nlpResult.getFulfillment().getSpeech()));
    }

    @Controller(events = {EventType.DIRECT_MESSAGE}, next = "confirmPassword")
    public void enterPassword(WebSocketSession session, Event event) {
        if(event.getText().trim().length() < 7) {
            reply(session, event, new Message("The password is too short, please ask me to change your password again when you are ready"));
            stopConversation(event);
        } else {
            reply(session, event, new Message("Good - now confirm the password"));
            channelValues.put(event.getChannelId(), event.getText().trim());
            nextConversation(event);
        }
    }

    @Controller(events = {EventType.DIRECT_MESSAGE})
    public void confirmPassword(WebSocketSession session, Event event) {
        if(!event.getText().trim().equals(channelValues.get(event.getChannelId()))) {
            reply(session, event, new Message("The passwords didn't match, please ask me to change your password again when you are ready"));
        } else {
            reply(session, event, new Message("Excellent - I've changed your password"));
        }
        stopConversation(event);
    }
}
