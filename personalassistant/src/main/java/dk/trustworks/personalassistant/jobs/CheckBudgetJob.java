package dk.trustworks.personalassistant.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.*;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.personalassistant.client.RestClient;
import dk.trustworks.personalassistant.dto.timemanager.*;
import dk.trustworks.personalassistant.dto.timemanager.User;
import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.jooby.quartz.Scheduled;

import java.util.*;

/**
 * Created by hans on 15/06/16.
 */
public class CheckBudgetJob {

    private final RestClient restClient = new RestClient();
    private SlackWebApiClient halWebApiClient = SlackClientFactory.createWebApiClient(System.getProperty("HAL_SLACK_TOKEN"));


}
