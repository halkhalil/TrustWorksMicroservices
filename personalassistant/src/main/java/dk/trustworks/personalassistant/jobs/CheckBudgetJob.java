package dk.trustworks.personalassistant.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import dk.trustworks.personalassistant.client.RestClient;

/**
 * Created by hans on 15/06/16.
 */
public class CheckBudgetJob {

    private final RestClient restClient = new RestClient();
    private SlackWebApiClient halWebApiClient = SlackClientFactory.createWebApiClient(System.getProperty("HAL_SLACK_TOKEN"));


}
