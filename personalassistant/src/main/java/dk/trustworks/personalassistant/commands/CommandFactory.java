package dk.trustworks.personalassistant.commands;

import dk.trustworks.personalassistant.client.SlackResponseClient;
import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;
import dk.trustworks.personalassistant.model.ActionType;

import java.util.HashMap;

import static dk.trustworks.personalassistant.client.SlackResponseClient.*;

/**
 * Created by hans on 31/05/16.
 */
public class CommandFactory {
    private final HashMap<ActionType, Command> commands;

    private static CommandFactory instance;

    private CommandFactory() {
        commands = new HashMap<>();
    }

    public void addCommand(ActionType action, Command command) {
        commands.put(action, command);
    }

    public void executeCommand(Result intentOutcome, SlackSlashCommand slackSlashCommand) {
        System.out.println("CommandFactory.executeCommand");
        System.out.println("intentOutcome = [" + intentOutcome + "], slackSlashCommand = [" + slackSlashCommand + "]");

        if(intentOutcome.getSource().trim().equals("domains")) {
            sendResponse(slackSlashCommand.response_url, new SlackMessage(intentOutcome.getFulfillment().getSpeech(), "in_channel"));
            return;
        }

        if (commands.containsKey(ActionType.valueOf(intentOutcome.getAction()))) {
            commands.get(ActionType.valueOf(intentOutcome.getAction())).execute(intentOutcome, slackSlashCommand);
        }

    }

    public void listCommands() {
        //System.out.println("Enabled commands: " + commands.keySet().stream().collect(Collectors.joining(", ")));
    }

    /* Factory pattern */
    public static CommandFactory init() {
        if(instance==null) instance = new CommandFactory();
        instance.addCommand(ActionType.WhoAreYou, new WhoAreYouCommand());
        instance.addCommand(ActionType.WhatAreYou, new WhatAreYouCommand());
        instance.addCommand(ActionType.WhatIsYourDirective, new WhatIsYourDirectiveCommand());
        instance.addCommand(ActionType.ShowPhotos, new PhotosCommand());
        instance.addCommand(ActionType.SendTemplate, new TemplateCommand());
        instance.addCommand(ActionType.FileSearch, new FileSearchCommand());
        //cf.addCommand("Light off", (slackSlashCommand) -> System.out.println("Light turned off"));

        return instance;
    }
}
