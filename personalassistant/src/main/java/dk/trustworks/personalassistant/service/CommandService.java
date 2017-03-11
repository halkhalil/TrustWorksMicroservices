package dk.trustworks.personalassistant.service;

import dk.trustworks.personalassistant.commands.CommandFactory;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;
import dk.trustworks.personalassistant.nlp.ApiAIClient;

/**
 * Created by hans on 30/05/16.
 */
public class CommandService {

    private final CommandFactory commandFactory;

    public CommandService() {
        commandFactory = CommandFactory.init();
    }


    // command =
    // SlackSlashCommand{token='P5i2tVZ0P9WGGMsT0FkRhks2',
    // team_id='T036JELTL',
    // team_domain='dk.trustworks',
    // channel_id='D13E9H2N9',
    // channel_name='directmessage',
    // user_id='U036JELTN',
    // user_name='hans',
    // command='/mother',
    // text='test',
    // response_url='https://hooks.slack.com/commands/T036JELTL/46826048199/1nsSqrp5LgT7gLzFQ2Is5cQf'}

    public void create(SlackSlashCommand command) {
        System.out.println("CommandService.create");
        System.out.println("command = [" + command + "]");

        commandFactory.executeCommand(ApiAIClient.sendQuery(command.text), command);


    }
}
