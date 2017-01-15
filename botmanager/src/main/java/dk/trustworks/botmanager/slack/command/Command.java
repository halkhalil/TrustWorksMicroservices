
package dk.trustworks.botmanager.slack.command;


import dk.trustworks.botmanager.network.slack.dto.SlackSlashCommand;
import dk.trustworks.botmanager.nlp.dto.Result;

/**
 * Created by hans on 31/05/16.
 */

@FunctionalInterface
public interface Command {
    void execute(Result intentOutcome, SlackSlashCommand command);
}
