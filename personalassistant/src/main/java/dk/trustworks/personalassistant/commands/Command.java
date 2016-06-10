package dk.trustworks.personalassistant.commands;

import dk.trustworks.personalassistant.dto.nlp.Result;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;

/**
 * Created by hans on 31/05/16.
 */

@FunctionalInterface
public interface Command {
    void execute(Result intentOutcome, SlackSlashCommand command);
}
