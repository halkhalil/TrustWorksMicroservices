package dk.trustworks.cronmanager.client;

import com.mashape.unirest.http.Unirest;
import dk.trustworks.framework.network.Locator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    private static final Logger log = LogManager.getLogger(RestClient.class);

    public void updateBudgetByMonthAndYear(int month, int year) {
        log.entry(month, year);
        try {
            Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/projectbudgets/budgetcleanup")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke kalde: budgetcleanup", e);
        }
        log.exit();
    }
}
